#! /usr/bin/env groovy

import groovy.xml.*
import groovy.xml.XmlNodePrinter
import java.util.Set
import java.util.HashSet

/* 
   script:   update_localextensions
   language: Groovy
   author:   Pierre Billon, Samuel Yang
   version:  1.1
   date:     27 July 2022

   invocation: groovy update_localextensions {add|remove} "ext1,ext2,etc" <path of localextensions.xml>

   description: Adds or removes given extensions from localextensions.xml
*/

if (args.size() != 3) {
    args.each {println it}
	println "\nUsage: groovy update_localextensions.groovy add|remove '<ext1>,<ext2>,etc' <path of localextensions.xml>\n"
	return 0
}


// Read and test arguments

def action = args[0]
def additionalExtensions = args[1].split(',')
def localextensionsFile = args[2]
def error = false


if (!action.equals('add') && !action.equals('remove')) {
    println "*ERROR* First parameter (action) must be: 'add' or 'remove'"
    error = true
}
if (!new File(localextensionsFile).exists()) {
    println "*ERROR* Cannot find the localextensions.xml file at: " + localextensionsFile
    error = true
}
if (error) {
    System.exit(-1)
}


// Parse XML files
def localExtensions = new XmlParser().parse(localextensionsFile)


// Add or remove nodes in additionalExtensions to/from localExtensions
if (action.equals('add')) {
	  additionalExtensions.each { ext ->
        if (localExtensions.extensions.'*'.find { ext.equals(it.@name) } == null) {
            localExtensions.children()[0].appendNode("extension", ["name": ext])
        }
    }
} else if (action.equals('remove')) {
    additionalExtensions.each { ext ->
        def node = localExtensions.extensions.extension.find { target -> target.@name.equals(ext) }
        if (node != null) { 
            node.parent().remove(node)
        }
    }
} else {
    println "*ERROR* First parameter (action) must be: 'add' or 'remove'"
}

// Write out the resulting xml

new File( localextensionsFile ).withPrintWriter { out ->
  new XmlNodePrinter(out).print(localExtensions)
}
