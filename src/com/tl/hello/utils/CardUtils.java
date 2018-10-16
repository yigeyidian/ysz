package com.tl.hello.utils;

import java.io.PrintStream;
import java.util.Random;

public class CardUtils {
	private static String[] cards = new String[52];

	public static void initCard() {
		for (int i = 1; i <= 52; i++) {
			if (i / 13 == 0) {
				cards[(i - 1)] = ("h" + i % 13);
			} else if (i / 13 == 1) {
				cards[(i - 1)] = ("f" + i % 13);
			} else if (i / 13 == 2) {
				cards[(i - 1)] = ("t" + i % 13);
			} else {
				cards[(i - 1)] = ("m" + i % 13);
			}
		}
	}

	public static String getCard() {
		int i = 0;
		String card = "";
		while (i < 3) {
			int ranNumber = new Random().nextInt(cards.length);
			if (cards[ranNumber] != "") {
				card = card + "-" + cards[ranNumber];
				cards[ranNumber] = "";
				i++;
			}
		}
		return card.substring(1);
	}

	public static int dealcard() {
		int[] total = new int[52];

		int[][] player = new int[6][3];

		int leftNum = 52;

		Random random = new Random();
		for (int i = 0; i < total.length; i++) {
			total[i] = (i + 1);
		}
		for (int i = 0; i < 18; i++) {
			for (int j = 0; j < player.length; j++) {
				int ranNumber = random.nextInt(leftNum);

				player[j][i] = total[ranNumber];

				total[ranNumber] = total[(leftNum - 1)];

				leftNum--;
			}
		}
		for (int i = 0; i < player.length; i++) {
			for (int j = 0; j < player[i].length; j++) {
				System.out.print("��" + player[i][j]);
			}
			System.out.println();
		}
		for (int i = 0; i < 8; i++) {
			System.out.print("��" + total[i]);
		}
		System.out.println();
		return 0;
	}

	public static int is235(String card){
		String[] cards = card.split(",");
		String c1 = cards[0].substring(1);
		String c2 = cards[1].substring(1);
		String c3 = cards[2].substring(1);
		
		if(c1.equals("2") && c2.equals("3") && c3.equals("5")){
			return 1;
		}
		
		if(c1.equals("2") && c2.equals("5") && c3.equals("3")){
			return 1;
		}
		
		if(c1.equals("3") && c2.equals("2") && c3.equals("5")){
			return 1;
		}
		
		if(c1.equals("3") && c2.equals("5") && c3.equals("2")){
			return 1;
		}
		
		if(c1.equals("5") && c2.equals("2") && c3.equals("3")){
			return 1;
		}
		if(c1.equals("5") && c2.equals("3") && c3.equals("2")){
			return 1;
		}
		return -1;
	}
	
	public static int isAAA(String card) {
		String[] cards = card.split(",");
		String c1 = cards[0].substring(1);
		String c2 = cards[1].substring(1);
		String c3 = cards[2].substring(1);
		if ((c1.equals(c2)) && (c2.equals(c3))) {
			return Integer.parseInt(c2);
		}
		return -1;
	}

	public static int isTH(String card) {
		String[] cards = card.split(",");
		if ((cards[0].charAt(0) == cards[1].charAt(0))
				&& (cards[2].charAt(0) == cards[1].charAt(0))) {
			return getMax(card);
		}
		return -1;
	}

	public static int isSort(String card) {
		String[] cards = card.split(",");
		int c1 = Integer.parseInt(cards[0].substring(1));
		int c2 = Integer.parseInt(cards[1].substring(1));
		int c3 = Integer.parseInt(cards[2].substring(1));
		if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
			return c3;
		}
		if ((c1 == c2 + 1) && (c2 == c3 + 1)) {
			return c1;
		}
		if ((c2 == c3 + 1) && (c3 == c1 + 1)) {
			return c2;
		}
		if ((c1 == c3 + 1) && (c3 == c2 + 1)) {
			return c1;
		}
		if ((c1 == c2 + 1) && (c3 == c1 + 1)) {
			return c3;
		}
		if ((c1 == c3 + 1) && (c2 == c1 + 1)) {
			return c2;
		}
		return -1;
	}

	public static int isTHS(String card) {
		if (isTH(card) == isSort(card)) {
			return isSort(card);
		}
		return -1;
	}

	public static int isDouble(String card) {
		String[] cards = card.split(",");
		int c1 = Integer.parseInt(cards[0].substring(1));
		int c2 = Integer.parseInt(cards[1].substring(1));
		int c3 = Integer.parseInt(cards[2].substring(1));
		if ((c1 == c2) || (c1 == c3)) {
			return c1;
		}
		if (c2 == c3) {
			return c2;
		}
		return -1;
	}

	public static int isDouble2(String card) {
		String[] cards = card.split(",");
		int c1 = Integer.parseInt(cards[0].substring(1));
		int c2 = Integer.parseInt(cards[1].substring(1));
		int c3 = Integer.parseInt(cards[2].substring(1));
		if (c1 == c2) {
			return c3;
		}
		if (c2 == c3) {
			return c1;
		}
		return c2;
	}

	public static int getMax(String card) {
		String[] cards = card.split(",");
		int c1 = Integer.parseInt(cards[0].substring(1));
		int c2 = Integer.parseInt(cards[1].substring(1));
		int c3 = Integer.parseInt(cards[2].substring(1));
		if ((c1 > c2) && (c1 > c3)) {
			return c1;
		}
		if ((c2 > c1) && (c2 > c3)) {
			return c2;
		}
		return c3;
	}
}
