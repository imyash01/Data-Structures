package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	String update = expr.replaceAll(" ", "");
    	int length = update.length();
		int bracketIndex = 0;
    	StringTokenizer st1 = new StringTokenizer(update, delims); 
           while (st1.hasMoreTokens()) {
        	  String temp = st1.nextToken();
        	  int index = 0;
        	  if(!Character.isDigit(temp.charAt(0))) {
        			 index = update.indexOf(temp, bracketIndex) + (temp.length());
        			 if(length - index > 0) {
	         			 boolean isArray =  update.charAt(index) == '[';
	         			 if((length - update.indexOf(temp) > 1 && isArray)){
	                		  Array temp1 = new Array(temp);
	                		  if(arrays.contains(temp1)) {
	                    		  bracketIndex = (update.indexOf(temp, bracketIndex) + (temp.length()));
	                    		  continue;
	                    	  }
	                		  else {
	                		  arrays.add(temp1);
	                		  bracketIndex = (update.indexOf(temp, bracketIndex) + (temp.length()));
	                		  }
	                	  }
	                	  else {
	                		  Variable temp2 = new Variable(temp);
	                		  if(vars.contains(temp2)) {
	                    		  continue;
	                    	  }
	                		  vars.add(temp2);
	                		  }
        			 }
        			 else {
        				 Variable temp2 = new Variable(temp);
	               		  if(vars.contains(temp2)) {
	                   		  continue;
	                   	  }
	               		  vars.add(temp2);
        			 }
        	  }
           } 	    		  
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    private static Float applyOpr(Float value1, Float value2, char oprand) {
    	Float result = (float) 0.0;
    	switch(oprand) {
		case '*':
			result = (value2 * value1);
			break;
		case '+':
			result = (value2 + value1);
			break;
		case '-':
			result = (value2 - value1);
			break;
		case '/':
			result = (value1 / value2);
			break;
		}
		return result;
    }
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	String updateExpr = expr.replaceAll(" ", "");
    	StringTokenizer st1 = new StringTokenizer(updateExpr, delims, true);
    	Stack<Float> num = new Stack<Float>();
		Stack <Character> opr = new Stack<Character>();
		Stack<Float> reverseNum = new Stack<Float>();
		Stack <Character> reverseOpr = new Stack<Character>();
		Stack <Character> prnt = new Stack<Character>();
		Stack <Character> brck = new Stack<Character>();
		Stack <Character> altPrnt = new Stack<Character>();
		Stack <Character> altBrck = new Stack<Character>();
		int exprIndex = -1;
		int firstPrntIndex = 0;
		int firstBrckIndex = 0;
		int arrayIndex = 0;
		boolean prntSkip = false;
		boolean brckSkip = false;
		int index = 0;
    	while(st1.hasMoreTokens()) {
    		String curr = st1.nextToken();
    		exprIndex += curr.length();
    		if(Character.isDigit(curr.charAt(0))) {
    			Float currNum = Float.parseFloat(curr);
    			if(prntSkip || brckSkip)
    				continue;
    			num.push(currNum);
    		}
    		else{
    			if(curr.equals("(")) {
    				if(brckSkip)
        				continue;
    				prnt.push(curr.charAt(0));
    				if(!prntSkip)
    					firstPrntIndex = exprIndex;
    				prntSkip = true;
    			}
    			if(curr.equals(")")) {
    				if(brckSkip)
        				continue;
    				if(prnt.size() > 1) {
    					prnt.pop();
    				}
    				else {
	    				prnt.pop();
	    				prntSkip = false;
	    				num.push(evaluate(updateExpr.substring(firstPrntIndex + 1, exprIndex), vars, arrays));
    				}
    			}
    			if(curr.equals("[")) {
    				if(prntSkip)
        				continue;
    				brck.push(curr.charAt(0));
    				if(!brckSkip) {
	    				firstBrckIndex = exprIndex;
	    				brckSkip = true;
    				}
    			}
    			if(curr.equals("]")) {
    				if(prntSkip)
        				continue;
    				if(brck.size() > 1) {
    					brck.pop();
    				}
    				else {
	    				brck.pop();
	    				brckSkip = false;
	    				index = (int)evaluate(updateExpr.substring(firstBrckIndex + 1, exprIndex), vars, arrays);
	    				num.push((float)arrays.get(arrayIndex).values[index]);
    				}
    			}
    			if(brckSkip)
    				continue;
    			Array temp1 = new Array(curr);
    			if(arrays.contains(temp1))
					arrayIndex = arrays.indexOf(temp1);
    			if(prntSkip)
    				continue;
    			if(curr.length() >= 1) {
    				Variable temp = new Variable(curr);
    				if(vars.contains(temp))
    					num.push((float)vars.get(vars.indexOf(temp)).value);    				
    			}
    			if(curr.equals("*")||curr.equals("/")) {
    				//exprIndex++;
    				String nextToken = "";
    				char operator = curr.charAt(0);
    				boolean altPrntSkip = false;
    				boolean altBrckSkip = false;
    				int arrayIndex1 = 0;
					while(st1.hasMoreTokens()) {
    					nextToken = st1.nextToken();
    					exprIndex += nextToken.length();
    				if(Character.isDigit(nextToken.charAt(0))) {
    					if(altPrntSkip || altBrckSkip) {
    						continue;
    					}
    					num.push(applyOpr(num.pop(),Float.valueOf(nextToken), operator));
    					break;
    				}
    				else {
    					if(nextToken.length() >= 1) {
    	    				Variable temp5 = new Variable(nextToken);
    	    				if(vars.contains(temp5)) {
    	    					if(altPrntSkip || altBrckSkip) {
    	    						continue;
    	    					}
    	    					else {
	    	    					num.push(applyOpr(num.pop(),(float)vars.get(vars.indexOf(temp5)).value, curr.charAt(0)));
	    	    					break;
    	    					}
    	    				}
    	    				else {
    	    					Array temp6 = new Array(nextToken);
    	    	    			if(arrays.contains(temp6)) {
    	    	    				if(altBrckSkip || altPrntSkip)
        	    	    				continue;
    	    	    				arrayIndex1 = arrays.indexOf(temp6);
    	    	    			}
    	    				}
    					}
    					if(nextToken.equals("(")) {
    	    				if(altBrckSkip)
    	        				continue;
    	    				altPrnt.push(nextToken.charAt(0));
    	    				if(!altPrntSkip)
    	    					firstPrntIndex = exprIndex;
    	    				altPrntSkip = true;
    	    			}
    					if(nextToken.equals(")")) {
    	    				if(altBrckSkip)
    	        				continue;
    	    				if(altPrnt.size() > 1) {
    	    					altPrnt.pop();
    	    				}
    	    				else {
    		    				altPrnt.pop();
    		    				altPrntSkip = false;
    		    				float rec = evaluate(updateExpr.substring(firstPrntIndex + 1, exprIndex), vars, arrays);
    		    				num.push(applyOpr(num.pop(),rec, operator));
    		    				break;
    	    				}
    	    			}
    					if(nextToken.equals("[")) {
    	    				if(altPrntSkip)
    	        				continue;
    	    				altBrck.push(curr.charAt(0));
    	    				if(!altBrckSkip) {
    		    				firstBrckIndex = exprIndex;
    		    				altBrckSkip = true;
    	    				}
    	    			}
    	    			if(nextToken.equals("]")) {
    	    				if(altPrntSkip)
    	        				continue;
    	    				if(altBrck.size() > 1) {
    	    					altBrck.pop();
    	    				}
    	    				else {
    		    				altBrck.pop();
    		    				altBrckSkip = false;
    		    				index = (int)evaluate(updateExpr.substring(firstBrckIndex + 1, exprIndex), vars, arrays);
    		    				float value = (float)arrays.get(arrayIndex1).values[index];
    		    				num.push(applyOpr(num.pop(),value, operator));
    	    				}
    	    			}
    					}
    				}
    			}
    			if(curr.equals("+")||curr.equals("-"))
    				opr.push(curr.charAt(0));
    		}
    	}
    	if(opr.isEmpty()) {
    		return num.pop();
    	}
    	while(!num.isEmpty()) {
    		reverseNum.push(num.pop());
    	}
    	while(!opr.isEmpty()) {
    		reverseOpr.push(opr.pop());
    	}
    	while(!reverseOpr.isEmpty()){
    		float temp1 = reverseNum.pop();
    		float temp2 = reverseNum.pop();
    		reverseNum.push(applyOpr(temp2, temp1, reverseOpr.pop()));
    	}
    		return reverseNum.pop();
    }
}

