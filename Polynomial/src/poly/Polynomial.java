package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node ptr1 = poly1, ptr2 = poly2, front = null, currentptr = front, temp = null;
		boolean LLcreated = false;
		while(ptr1 != null && ptr2 != null) {	
			if(ptr1.term.degree == ptr2.term.degree) {
				float sumOfCoeff = ptr1.term.coeff + ptr2.term.coeff;
				if(sumOfCoeff == 0) {
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;					
					continue;
				}
				if(!LLcreated) {
					front = new Node (sumOfCoeff,ptr1.term.degree, null);
					LLcreated = true;
					currentptr = front;
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}
				else {
					temp = new Node (sumOfCoeff,ptr1.term.degree,null);
					currentptr.next = temp;
					currentptr = temp;
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}
			}
			else if(ptr1.term.degree > ptr2.term.degree) {
				if(!LLcreated) {
					front = new Node (ptr2.term.coeff,ptr2.term.degree, null);
					LLcreated = true;
					currentptr = front;
					ptr2 = ptr2.next;
				}
				else {
					temp = new Node(ptr2.term.coeff,ptr2.term.degree, null);
					currentptr.next = temp;
					currentptr = temp;
					ptr2 = ptr2.next;
				}
			}
			else {
				if(!LLcreated) {
					front = new Node (ptr1.term.coeff,ptr1.term.degree, null);
					LLcreated = true;
					currentptr = front;
					ptr1 = ptr1.next;
				}
				else {
					temp= new Node(ptr1.term.coeff,ptr1.term.degree, null);
					currentptr.next = temp;
					currentptr = temp;
					ptr1 = ptr1.next;
				}
			}
		}
		while(ptr1 != null || ptr2 != null) {
			if(ptr1 != null) {
				if(!LLcreated) {
					front = new Node (ptr1.term.coeff,ptr1.term.degree, null);
					LLcreated = true;
					currentptr = front;
					ptr1 = ptr1.next;
				}
				else {
					temp = new Node(ptr1.term.coeff,ptr1.term.degree, null);
					currentptr.next = temp;
					currentptr = temp;
					ptr1 = ptr1.next;
				}
			}
			else if(ptr2 != null){
				if(!LLcreated) {
					front = new Node (ptr2.term.coeff,ptr2.term.degree, null);
					LLcreated = true;
					currentptr = front;
					ptr2 = ptr2.next;
				}
				else {
					temp = new Node(ptr2.term.coeff,ptr2.term.degree, null);
					currentptr.next = temp;
					currentptr = temp;
					ptr2 = ptr2.next;
				}
			}
		}
		return front;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node ptr = poly1, ptr1 = poly2, front = null, currentptr = front, temp = null, currentPoly = null;
		boolean LLcreated = false;
		if((ptr1 == null && ptr != null) || ptr != null && ptr == null) {
			return null;
		}
		while(ptr != null) {
			while(ptr1 != null) {
			float multiplied = ptr.term.coeff * ptr1.term.coeff;
			int newDegree = ptr.term.degree + ptr1.term.degree;
			if(!LLcreated) {
				front = new Node(multiplied, newDegree, null);
				currentptr = front;
				LLcreated = true;
				ptr1 = ptr1.next;
			}
			else {
				temp= new Node(multiplied,newDegree, null);
				currentptr.next = temp;
				currentptr = temp;
				ptr1 = ptr1.next;
			}
			}
			if(currentPoly == null) {
				ptr = ptr.next;
				currentPoly = front;
				front = null;
				LLcreated = false;
				ptr1 = poly2;
			}
			else {
				currentPoly = add(currentPoly, front);
				ptr = ptr.next;
				front = null;
				LLcreated = false;
				ptr1 = poly2;
			}
			
		}
		return currentPoly;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		Node ptr = poly;
		float sum = 0;
		while(ptr != null) {
			sum += (ptr.term.coeff * Math.pow(x, ptr.term.degree));
			ptr = ptr.next;
		}
		return sum;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
