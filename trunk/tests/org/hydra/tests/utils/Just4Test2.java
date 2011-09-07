package org.hydra.tests.utils;

public class Just4Test2 {
	public static final int FILE_TYPE_UNKNOWN = 0;
	public static final int FILE_TYPE_IMAGE = 1;
	public static final int FILE_TYPE_COMPRESSED = FILE_TYPE_IMAGE << 1;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		print_mask(FILE_TYPE_UNKNOWN);
		print_mask(FILE_TYPE_IMAGE);
		print_mask(FILE_TYPE_COMPRESSED);
	}

	private static void print_mask(int num) {
		for (int i = 0; i < 32; i++) {
			System.out.print((num >> i) == 1 ? "1" : 0);
			if(((i+1) % 4) == 0 && i != 0)
				System.out.print(" ");
		}
		System.out.println();
	}

}
