package org.hydra.tests.utils;


public class Just4Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String haystack = "Hello dolly";
		String needle = "dolly";
		KMP(haystack.toCharArray(), needle.toCharArray());
	}

	static void KMP (char[] haystack, char[] needle) {
		int m = needle.length;
		int n = haystack.length;
		int[] pf = new int[m];
		pf[0] = -1;
		//Вычисление префикс-функции
		for(int i = 1; i < m; i ++) {
			pf[i] = pf[i - 1] + 1;
			while(pf[i] > 0 && needle[i - 1] != needle[pf[i] - 1])
				pf[i] = pf[pf[i] - 1] + 1;
		}
		//Сопоставление образца
		for(int i = 0, j = 0; i < n; i ++) {
			while(j > 0 && needle[j] != haystack[i])
				j = pf[j];
			if(needle[j] == haystack[i])
				j ++;
			if(j == m) {
				//Образец обнаружен на сдвиге i - m + 1
				//Ищем следующее вхождение образца
				j = pf[j];
			}
		}
	}
	
	
}
