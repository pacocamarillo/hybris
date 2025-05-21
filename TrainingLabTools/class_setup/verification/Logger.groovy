class Logger{
	def logs = new LinkedList<>()
	def errors = new LinkedList<>()
	def separator = new String("\n====================================================================\n")
	
	def hasError(){
		errors.size() > 0
	}
	
	def errorNum(){
		errors.size()
	}
	
	def addError(err){
		errors.add(err)
		logs.add(err)
	}
	
	def addLog(log){
		logs.add(log)
	}
	
	def printOutputSummary(){
		if (hasError()){
			println "You have ${errorNum()} error" + ((errorNum()==1)?"":"s") + "!!!"
			println separator
		} else{
			println 'Congrats! Your solution is valid!'
		}
	}
	
	def void printOutputLog(){
	  	printOutputSummary()
	  	if (hasError()){
			println 'ERRORS:'
			errors.each {println it}
	    }
		println separator
		println 'LOGS:'
		logs.each {println it}
	}

}