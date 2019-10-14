package com.fanggeek.teams.api.controller;

import java.util.Arrays;
import java.util.Comparator;

public class Demo {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileNames[] = { "1栋", "1a栋", "5栋", "10栋", "1A", "1景夕阁", "2栋", "a栋", "A栋", "A景阳", "b栋", "B栋", "B1栋", "景夕阁1栋", "景夕阁2栋", "景夕阁2a栋", "景夕阁a栋", "景夕阁b栋", "景夕阁B3栋", "景阳阁1栋", "景阳阁2栋", "景阳阁a栋", "景阳阁B栋" };
		char chFileNames[][] = new char[fileNames.length][];
		String[] oldSortedNames = new String[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			chFileNames[i] = fileNames[i].toCharArray();
			oldSortedNames[i] = fileNames[i];
		}

		// Arrays.sort(fileNames, StrLogicCmp);
		Arrays.sort(chFileNames, ChsLogicCmp);
//		System.out.println("_Random_" + "\t" + "_Tradion_" + "\t" + "_Target_");
		String line;
		for (int i = 0; i < fileNames.length; i++) {
//			line = fileNames[i] + (fileNames[i].length() >= 8 ? "\t" : "\t\t");
//			line += oldSortedNames[i] + (oldSortedNames[i].length() >= 8 ? "\t" : "\t\t");
			line = new String(chFileNames[i]);
			System.out.println(line);

		}

	}

	// "f01s2s22", "f1s02s2"
	static Comparator<char[]> ChsLogicCmp = new Comparator<char[]>() {
		class Int {
			public int i;
		}

		public int findDigitEnd(char[] arrChar, Int at) {
			int k = at.i;
			char c = arrChar[k];
			boolean bFirstZero = (c == '0');
			while (k < arrChar.length) {
				c = arrChar[k];
				// first non-digit which is a high chance.
				if (c > '9' || c < '0') {
					break;
				} else if (bFirstZero && c == '0') {
					at.i++;
				}
				k++;
			}
			return k;
		}

		@Override
		public int compare(char[] a, char[] b) {
			if (a != null || b != null) {
				Int aNonzeroIndex = new Int();
				Int bNonzeroIndex = new Int();
				int aIndex = 0, bIndex = 0, aComparedUnitTailIndex, bComparedUnitTailIndex;

//              Pattern pattern = Pattern.compile("D*(d+)D*");  
//              Matcher matcher1 = pattern.matcher(a);  
//              Matcher matcher2 = pattern.matcher(b);  
//              if(matcher1.find() && matcher2.find()) {  
//                  String s1 = matcher1.group(1);  
//                  String s2 = matcher2.group(1);  
//              }  

				while (aIndex < a.length && bIndex < b.length) {
					// aIndex <
					aNonzeroIndex.i = aIndex;
					bNonzeroIndex.i = bIndex;
					aComparedUnitTailIndex = findDigitEnd(a, aNonzeroIndex);
					bComparedUnitTailIndex = findDigitEnd(b, bNonzeroIndex);
					// compare by number
					if (aComparedUnitTailIndex > aIndex && bComparedUnitTailIndex > bIndex) {
						int aDigitIndex = aNonzeroIndex.i;
						int bDigitIndex = bNonzeroIndex.i;
						int aDigit = aComparedUnitTailIndex - aDigitIndex;
						int bDigit = bComparedUnitTailIndex - bDigitIndex;
						// compare by digit
						if (aDigit != bDigit)
							return aDigit - bDigit;
						// the number of their digit is same.
						while (aDigitIndex < aComparedUnitTailIndex) {
							if (a[aDigitIndex] != b[bDigitIndex])
								return a[aDigitIndex] - b[bDigitIndex];
							aDigitIndex++;
							bDigitIndex++;
						}
						// if they are equal compared by number, compare the number of '0' when start
						// with "0"
						// ps note: paNonZero and pbNonZero can be added the above loop "while", but it
						// is changed meanwhile.
						// so, the following comparsion is ok.
						aDigit = aNonzeroIndex.i - aIndex;
						bDigit = bNonzeroIndex.i - bIndex;
						if (aDigit != bDigit)
							return aDigit - bDigit;
						aIndex = aComparedUnitTailIndex;
						bIndex = bComparedUnitTailIndex;
					} else {
						if (a[aIndex] != b[bIndex])
							return a[aIndex] - b[bIndex];
						aIndex++;
						bIndex++;
					}

				}

			}
			return a.length - b.length;
		}

	};
}
