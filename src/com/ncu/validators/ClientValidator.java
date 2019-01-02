package com.ncu.validators;
import com.ncu.exceptions.*;

public class ClientValidator{

public boolean choiceValidator(int choice){

	ClientValidator object=new ClientValidator();
    try{
	object.checkChoice(choice);
    }
    catch(CheckChoiceException e){
    	 System.out.println(e+" Invalid input");
			// logger.error("\n \n"+e+prop.getProperty("emptyNameMessage")+"\n");
			return false;
		}
	return true;

}

/* Generate "CheckChoiceException" Exception if user enters invalid choice */
	private void checkChoice(int choice) throws CheckChoiceException {	
		if (choice < 1 || choice > 3) {
			throw new CheckChoiceException("");
		}
	}


		
} 