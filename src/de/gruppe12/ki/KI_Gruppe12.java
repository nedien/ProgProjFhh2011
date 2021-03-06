package de.gruppe12.ki;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import de.fhhannover.inform.hnefatafl.vorgaben.BoardContent;
import de.fhhannover.inform.hnefatafl.vorgaben.MoveStrategy;
import de.gruppe12.shared.*;

/**
 * Die Klasse KI_Gruppe12 ist die normale Spielstrategie
 * <p>
 * Copyright: (c) 2011
 * <p>
 * Company: Gruppe 12
 * <p>
 * 
 * @author Markus Blume, Lennart Henke
 * @version 1.3.3.7 24.01.2012
 */

public class KI_Gruppe12 implements MoveStrategy {
	private boolean commandLine;
	private int grpNr;
	private String name;
	private Board b;
	private Random r;

	/**
	 * Konstruktor
	 */

	public KI_Gruppe12() {
		grpNr = 12;
		name = "KI_Gruppe12";
		b = new Board();
		r = new Random();
		this.commandLine = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhhannover.inform.hnefatafl.vorgaben.MoveStrategy#getGroupNr()
	 */

	@Override
	public int getGroupNr() {
		return grpNr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhhannover.inform.hnefatafl.vorgaben.MoveStrategy#getStrategyName()
	 */

	@Override
	public String getStrategyName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return name + " Strategy, Gruppe" + grpNr;
	}

	/**
	 * Uebernimmt einen Move auf ein gegebenes Board
	 * 
	 * @param move
	 *            Zu uebernehmender Move
	 * @param board
	 *            Zugehoeriges Board
	 * @return
	 */

	private Board doMove(de.fhhannover.inform.hnefatafl.vorgaben.Move move,
			Board board) {
		if (move != null) {
			return RemoveCheck.checkForRemove(move, board, false);
		}
		return board;
	}

	/**
	 * 
	 * GenerateMoves() Erzeugt ein Array mit allen moeglichen Zuegen fuer jede
	 * Spielfigur, ohne zu pruefen ob der Zug erlaubt ist.
	 * 
	 * @return ArrayList mit allen moeglichen Moves
	 */
	private ArrayList<Move> generateMoves(BoardContent type) {
		ArrayList<Move> mList = new ArrayList<Move>();
		ArrayList<Cell> cList = new ArrayList<Cell>();

		// prüft wo Steine sind
		for (int i = 0; i <= 12; i++) {
			for (int j = 0; j <= 12; j++) {
				// speichert nur wenn Typ gleich ist
				if (b.getCell(i, j).getContent() == type) {
					cList.add(new Cell(i, j, type));
				}
			}
		}

		// Generiert für jeden Stein Moves bis zum Ende des Spielfeldes. Ohne zu
		// prüfen, ob diese erlaubt sind
		for (int i = 0; i < cList.size(); i++) {
			int space_to_right = 12 - cList.get(i).getCol();
			int space_to_bottom = 12 - cList.get(i).getRow();
			for (int j = 1; j <= space_to_right; j++) {
				mList.add(new Move(cList.get(i), new Cell(cList.get(i).getCol()
						+ j, cList.get(i).getRow(), cList.get(i).getContent())));
			}
			for (int j = 1; j <= space_to_bottom; j++) {
				mList.add(new Move(cList.get(i), new Cell(
						cList.get(i).getCol(), cList.get(i).getRow() + j, cList
								.get(i).getContent())));
			}
			for (int j = cList.get(i).getCol(); j > 0; j--) {
				mList.add(new Move(cList.get(i), new Cell(cList.get(i).getCol()
						- j, cList.get(i).getRow(), cList.get(i).getContent())));
			}
			for (int j = cList.get(i).getRow(); j > 0; j--) {
				mList.add(new Move(cList.get(i), new Cell(
						cList.get(i).getCol(), cList.get(i).getRow() - j, cList
								.get(i).getContent())));
			}
		}
		return mList;
	}

	/**
	 * calculateValue Bewertet den Uebergebenen Move.
	 * 
	 * @return Bewertung des Moves
	 */
	private int calculateValue(Move move, boolean isDefTurn) {
		int rating = 0;

		// Abbrechen, wenn leerer Move uebergeben wurde
		if (move == null) {
			return -1;
		}

		// Abbrechen bei ungueltigem Move
		if (!MoveCheck.check(move, this.b, isDefTurn, false)) {
			return -1;
		}

		// Figur die Bewegt wird
		BoardContent p = move.getFromCell().getContent();
		// Felder in umgebung
		BoardContent c1 = b.getCell(move.getToCell().getCol(),
				move.getToCell().getRow() - 1).getContent();
		BoardContent c2 = b.getCell(move.getToCell().getCol() + 1,
				move.getToCell().getRow()).getContent();
		BoardContent c3 = b.getCell(move.getToCell().getCol(),
				move.getToCell().getRow() + 1).getContent();
		BoardContent c4 = b.getCell(move.getToCell().getCol() - 1,
				move.getToCell().getRow()).getContent();

		// Wenn Gegner und kein leeres Feld steigt Value
		if (p == BoardContent.ATTACKER) {
			if ((c1 != p) && (c1 != BoardContent.EMPTY)) {
				rating += 20;
			}
			if ((c2 != p) && (c2 != BoardContent.EMPTY)) {
				rating += 20;
			}
			if ((c3 != p) && (c3 != BoardContent.EMPTY)) {
				rating += 20;
			}
			if ((c4 != p) && (c4 != BoardContent.EMPTY)) {
				rating += 20;
			}
		}

		int toRow = move.getToCell().getRow();
		int toCol = move.getToCell().getCol();

		int fromRow = move.getFromCell().getRow();
		int fromCol = move.getFromCell().getCol();

		// extra Berechnung fuer Attacker
		// 0 1 2 3 4 5 6 7 8 9 10 11 12
		// 0|xx|10|10|xx|xx|xx|xx|xx|xx|xx|10|10|xx
		// 1|10|10|10|xx|xx|xx|xx|xx|xx|xx|10|10|10
		// 2|10|10|10|xx|xx|xx|xx|xx|xx|xx|10|10|10
		// 3|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 4|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 5|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 6|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 7|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 8|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 9|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 10|10|10|10|xx|xx|xx|xx|xx|xx|xx|10|10|10
		// 11|10|10|10|xx|xx|xx|xx|xx|xx|xx|10|08|10
		// 12|xx|10|10|xx|xx|xx|xx|xx|xx|xx|10|10|xx

		if (p == BoardContent.ATTACKER) {
			// Bewertung 10
			if ((((toRow == 1) && (toCol == 0))
					|| ((toRow == 0) && (toCol == 1))
					|| ((toRow == 0) && (toCol == 11))
					|| ((toRow == 1) && (toCol == 12))
					|| ((toRow == 11) && (toCol == 0))
					|| ((toRow == 12) && (toCol == 1))
					|| ((toRow == 12) && (toCol == 11))
					|| ((toRow == 11) && (toCol == 12))
					|| ((toRow == 0) && (toCol == 2))
					|| ((toRow == 1) && (toCol == 2))
					|| ((toRow == 2) && (toCol == 2))
					|| ((toRow == 2) && (toCol == 1))
					|| ((toRow == 1) && (toCol == 1))
					|| ((toRow == 2) && (toCol == 0))
					|| ((toRow == 10) && (toCol == 0))
					|| ((toRow == 10) && (toCol == 1))
					|| ((toRow == 10) && (toCol == 2))
					|| ((toRow == 11) && (toCol == 1))
					|| ((toRow == 11) && (toCol == 2))
					|| ((toRow == 12) && (toCol == 2))
					|| ((toRow == 10) && (toCol == 12))
					|| ((toRow == 10) && (toCol == 11))
					|| ((toRow == 10) && (toCol == 10))
					|| ((toRow == 11) && (toCol == 11))
					|| ((toRow == 11) && (toCol == 10))
					|| ((toRow == 12) && (toCol == 10))
					|| ((toRow == 0) && (toCol == 10))
					|| ((toRow == 1) && (toCol == 10))
					|| ((toRow == 1) && (toCol == 11))
					|| ((toRow == 2) && (toCol == 10))
					|| ((toRow == 2) && (toCol == 11)) || ((toRow == 2) && (toCol == 12)))
					&& (!((fromRow == 1) && (fromCol == 0))
							&& !((fromRow == 0) && (fromCol == 1))
							&& !((fromRow == 0) && (fromCol == 11))
							&& !((fromRow == 1) && (fromCol == 12))
							&& !((fromRow == 11) && (fromCol == 0))
							&& !((fromRow == 12) && (fromCol == 1))
							&& !((fromRow == 12) && (fromCol == 11))
							&& !((fromRow == 11) && (fromCol == 12))
							&& !((fromRow == 0) && (fromCol != 2))
							&& !((fromRow == 1) && (fromCol != 2))
							&& !((fromRow == 2) && (fromCol == 2))
							&& !((fromRow == 2) && (fromCol == 1))
							&& !((fromRow == 1) && (fromCol == 1))
							&& !((fromRow == 2) && (fromCol == 0))
							&& !((fromRow == 10) && (fromCol == 0))
							&& !((fromRow == 10) && (fromCol == 1))
							&& !((fromRow == 10) && (fromCol == 2))
							&& !((fromRow == 11) && (fromCol == 1))
							&& !((fromRow == 11) && (fromCol == 2))
							&& !((fromRow == 12) && (fromCol == 2))
							&& !((fromRow == 10) && (fromCol == 12))
							&& !((fromRow == 10) && (fromCol == 11))
							&& !((fromRow == 10) && (fromCol == 10))
							&& !((fromRow == 11) && (fromCol == 11))
							&& !((fromRow == 11) && (fromCol == 10))
							&& !((fromRow == 12) && (fromCol == 10))
							&& !((fromRow == 0) && (fromCol == 10))
							&& !((fromRow == 1) && (fromCol == 10))
							&& !((fromRow == 1) && (fromCol == 11))
							&& !((fromRow == 2) && (fromCol == 10))
							&& !((fromRow == 2) && (fromCol == 11)) && !((fromRow == 2) && (fromCol == 12))))

			{
				return rating + 550;
			}

			// Wenn Koenig in der Naehe, dann ziehe dahin!
			if ((c1 == BoardContent.KING)) {
				return rating + 500;
			}
			if ((c2 == BoardContent.KING)) {
				return rating + 500;
			}
			if ((c3 == BoardContent.KING)) {
				return rating + 500;
			}
			if ((c4 == BoardContent.KING)) {
				return rating + 500;
			}

		}
		// extra Berechnung für Koenig
		// 0 1 2 3 4 5 6 7 8 9 10 11 12
		// 0|10|08|08|06|06|02|01|02|06|06|08|08|10
		// 1|08|06|06|04|03|02|01|02|03|04|06|06|08
		// 2|08|06|06|04|03|02|01|02|03|04|06|06|08
		// 3|06|04|04|04|03|02|01|02|03|04|04|04|06
		// 4|06|03|03|03|03|02|01|02|03|03|03|03|06
		// 5|02|02|02|02|02|02|01|02|02|02|02|02|02
		// 6|01|01|01|01|01|01|01|01|01|01|01|01|01
		// 7|02|02|02|02|02|02|01|02|02|02|02|02|02
		// 8|06|03|03|03|03|02|01|02|03|03|03|03|0
		// 9|06|04|04|04|03|02|01|02|03|04|04|04|06
		// 10|08|06|06|04|03|02|01|02|03|04|06|06|08
		// 11|08|06|06|04|03|02|01|02|03|04|06|06|08
		// 12|10|08|08|06|06|02|01|02|06|06|08|08|10

		if (p == BoardContent.KING) {

			if (c1 == BoardContent.EMPTY) {
				rating += 500;
			}
			if (c2 == BoardContent.EMPTY) {
				rating += 500;
			}
			if (c3 == BoardContent.EMPTY) {
				rating += 500;
			}
			if (c4 == BoardContent.EMPTY) {
				rating += 500;
			}

			// Felder mit Bewertung 10
			if (((toRow == 0) && (toCol == 0))
					|| ((toRow == 0) && (toCol == 12))
					|| ((toRow == 12) && (toCol == 0))
					|| ((toRow == 12) && (toCol == 12))) {
				return rating + 10000;
			}
			// Felder mit Bewertung 8
			if (((toRow == 0) && (toCol == 1))
					|| ((toRow == 1) && (toCol == 0))
					|| ((toRow == 1) && (toCol == 0))
					|| ((toRow == 0) && (toCol == 2)) ||

					((toRow == 0) && (toCol == 11))
					|| ((toRow == 1) && (toCol == 12))
					|| ((toRow == 0) && (toCol == 10))
					|| ((toRow == 2) && (toCol == 12)) ||

					((toRow == 11) && (toCol == 0))
					|| ((toRow == 12) && (toCol == 1))
					|| ((toRow == 10) && (toCol == 0))
					|| ((toRow == 12) && (toCol == 2)) ||

					((toRow == 12) && (toCol == 11))
					|| ((toRow == 12) && (toCol == 12))
					|| ((toRow == 12) && (toCol == 10))
					|| ((toRow == 10) && (toCol == 12))) {
				return rating + 5000;
			}
			// Felder mit Bewertung 6:
			if (((toRow == 1) && (toCol == 2))
					|| ((toRow == 2) && (toCol == 2))
					|| ((toRow == 2) && (toCol == 1))
					|| ((toRow == 1) && (toCol == 1))
					|| ((toRow == 3) && (toCol == 0))
					|| ((toRow == 0) && (toCol == 3))
					|| ((toRow == 4) && (toCol == 0))
					|| ((toRow == 0) && (toCol == 4)) ||

					((toRow == 10) && (toCol == 1))
					|| ((toRow == 10) && (toCol == 2))
					|| ((toRow == 11) && (toCol == 1))
					|| ((toRow == 11) && (toCol == 2))
					|| ((toRow == 12) && (toCol == 9))
					|| ((toRow == 9) && (toCol == 12))
					|| ((toRow == 8) && (toCol == 0))
					|| ((toRow == 12) && (toCol == 4)) ||

					((toRow == 10) && (toCol == 11))
					|| ((toRow == 10) && (toCol == 10))
					|| ((toRow == 11) && (toCol == 11))
					|| ((toRow == 11) && (toCol == 10))
					|| ((toRow == 12) && (toCol == 3))
					|| ((toRow == 9) && (toCol == 0))
					|| ((toRow == 0) && (toCol == 8))
					|| ((toRow == 4) && (toCol == 12))
					|| ((toRow == 8) && (toCol == 12))
					|| ((toRow == 0) && (toCol == 9))
					|| ((toRow == 1) && (toCol == 3))
					|| ((toRow == 1) && (toCol == 10))
					|| ((toRow == 1) && (toCol == 11))
					|| ((toRow == 2) && (toCol == 10))
					|| ((toRow == 2) && (toCol == 11))
					|| ((toRow == 12) && (toCol == 8))) {
				return rating + 2000;
			}
			// Felder mit Bewertung 4
			if (((toRow == 3) && (toCol == 1))
					|| ((toRow == 3) && (toCol == 2))
					|| ((toRow == 3) && (toCol == 3))
					|| ((toRow == 2) && (toCol == 3)) ||

					((toRow == 9) && (toCol == 1))
					|| ((toRow == 9) && (toCol == 2))
					|| ((toRow == 9) && (toCol == 3))
					|| ((toRow == 10) && (toCol == 3))
					|| ((toRow == 11) && (toCol == 3)) ||

					((toRow == 1) && (toCol == 9))
					|| ((toRow == 2) && (toCol == 9))
					|| ((toRow == 3) && (toCol == 9))
					|| ((toRow == 3) && (toCol == 10))
					|| ((toRow == 3) && (toCol == 11))
					|| ((toRow == 3) && (toCol == 12))
					|| ((toRow == 9) && (toCol == 9))
					|| ((toRow == 10) && (toCol == 9))
					|| ((toRow == 11) && (toCol == 9)) ||

					((toRow == 9) && (toCol == 10))
					|| ((toRow == 9) && (toCol == 11))

			) {
				return rating + 1000;
			}

			// Felder mit Bewertung 3: LO:
			if (((toRow == 4) && (toCol == 1))
					|| ((toRow == 4) && (toCol == 2))
					|| ((toRow == 4) && (toCol == 3))
					|| ((toRow == 4) && (toCol == 4)) ||

					((toRow == 1) && (toCol == 4))
					|| ((toRow == 2) && (toCol == 4))
					|| ((toRow == 3) && (toCol == 4)) ||

					((toRow == 8) && (toCol == 1))
					|| ((toRow == 8) && (toCol == 2))
					|| ((toRow == 8) && (toCol == 3))
					|| ((toRow == 8) && (toCol == 4))
					|| ((toRow == 9) && (toCol == 4))
					|| ((toRow == 10) && (toCol == 4))
					|| ((toRow == 11) && (toCol == 4)) ||

					((toRow == 1) && (toCol == 8))
					|| ((toRow == 2) && (toCol == 8))
					|| ((toRow == 3) && (toCol == 8))
					|| ((toRow == 4) && (toCol == 8))
					|| ((toRow == 4) && (toCol == 9))
					|| ((toRow == 4) && (toCol == 10))
					|| ((toRow == 4) && (toCol == 11)) ||

					((toRow == 8) && (toCol == 8)) ||

					((toRow == 8) && (toCol == 11))
					|| ((toRow == 8) && (toCol == 10))
					|| ((toRow == 8) && (toCol == 9))
					|| ((toRow == 9) && (toCol == 8))
					|| ((toRow == 10) && (toCol == 8))
					|| ((toRow == 11) && (toCol == 8))

			) {
				return rating + 50;
			}

			// Felder mit Bewertung 1:
			if (((toRow == 6) && (toCol == 0))
					|| ((toRow == 6) && (toCol == 1))
					|| ((toRow == 6) && (toCol == 2))
					|| ((toRow == 6) && (toCol == 3))
					|| ((toRow == 6) && (toCol == 4))
					|| ((toRow == 6) && (toCol == 5))
					|| ((toRow == 6) && (toCol == 8))
					|| ((toRow == 6) && (toCol == 9))
					|| ((toRow == 6) && (toCol == 10))
					|| ((toRow == 6) && (toCol == 11))
					|| ((toRow == 6) && (toCol == 12))
					|| ((toRow == 0) && (toCol == 6))
					|| ((toRow == 1) && (toCol == 6))
					|| ((toRow == 2) && (toCol == 6))
					|| ((toRow == 3) && (toCol == 6))
					|| ((toRow == 4) && (toCol == 6))
					|| ((toRow == 5) && (toCol == 6))
					|| ((toRow == 7) && (toCol == 6))
					|| ((toRow == 8) && (toCol == 6))
					|| ((toRow == 9) && (toCol == 6))
					|| ((toRow == 10) && (toCol == 6))
					|| ((toRow == 11) && (toCol == 6))
					|| ((toRow == 12) && (toCol == 6))) {
				return rating + 10;
			}
			if ((toRow == 6) && (toCol == 6)) {
				return rating + 1;
			}

		}

		// Berechnung fuer Verteidiger?
		// extra Berechnung fuer Deffender
		// 0 1 2 3 4 5 6 7 8 9 10 11 12
		// 0|xx|xx|10|xx|xx|xx|xx|xx|xx|xx|10|xx|xx
		// 1|xx|10|xx|xx|xx|xx|xx|xx|xx|xx|xx|10|xx
		// 2|10|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|10
		// 3|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 4|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 5|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx|xx
		// 6|xx|xx|xx|xx|xx|xx|KK|xx|xx|xx|xx|xx|xx
		// 7|xx|xx|xx|xx|xx|xx|xx|08|xx|xx|xx|xx|xx
		// 8|xx|xx|xx|xx|xx|xx|08|xx|08|xx|xx|xx|xx
		// 9|xx|xx|xx|xx|xx|08|xx|xx|xx|08|xx|xx|xx
		// 10|10|xx|xx|xx|08|xx|xx|xx|xx|xx|08|xx|xx
		// 11|xx|10|xx|08|xx|xx|xx|xx|xx|xx|xx|08|xx
		// 12|xx|xx|10|xx|xx|xx|xx|xx|xx|xx|08|08|xx
		if (p == BoardContent.DEFENDER) {
			if ((c1 == BoardContent.ATTACKER)) {
				rating += 20;
			}
			if ((c2 == BoardContent.ATTACKER)) {
				rating += 20;
			}
			if ((c3 == BoardContent.ATTACKER)) {
				rating += 20;
			}
			if ((c4 == BoardContent.ATTACKER)) {
				rating += 20;
			}

			if (((fromRow == 5) && (fromCol == 6))
					|| ((fromRow == 6) && (fromCol == 5))
					|| ((fromRow == 6) && (fromCol == 7))
					|| ((fromRow == 7) && (fromCol == 6))) {
				if (fromRow == toRow) {
					rating += Math.abs(fromCol - toCol) * 50;
				} else {
					rating += Math.abs(fromRow - toRow) * 50;
				}
			}

			if (((fromRow == 4) && (fromCol == 6))
					|| ((fromRow == 8) && (fromCol == 6))
					|| ((fromRow == 6) && (fromCol == 8))
					|| ((fromRow == 6) && (fromCol == 4))) {
				if (fromRow == toRow) {
					rating += Math.abs(fromCol - toCol) * 20;
				} else {
					rating += Math.abs(fromRow - toRow) * 20;
				}
			}

		}
		if (this.commandLine)
			System.out.println(move.toString() + "Value: " + rating);
		return rating;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhhannover.inform.hnefatafl.vorgaben.MoveStrategy#calculateAttackerMove
	 * (de.fhhannover.inform.hnefatafl.vorgaben.Move, int)
	 */
	@Override
	public de.fhhannover.inform.hnefatafl.vorgaben.Move calculateAttackerMove(
			de.fhhannover.inform.hnefatafl.vorgaben.Move lastMove, int thinkTime) {
		// Startzeit festhalten
		long startTime = System.nanoTime();

		// Lokale Variablen
		int rating;
		ArrayList<Integer> possibleMoves;
		TreeMap<Integer, ArrayList<Integer>> ratedMoves = new TreeMap<Integer, ArrayList<Integer>>();

		// Uebergebenen Move auf KI-internem Board uebernehmen
		this.b = doMove(lastMove, this.b);

		// ArrayList mit allen erlauben Moves generieren
		ArrayList<Move> generatedMoves = generateMoves(BoardContent.ATTACKER);

		// Moves nach Rating sortiert in TreeMap speichern
		for (int i = 0; i < generatedMoves.size(); i++) {

			// Wenn nur noch weniger als 5ms der Zeitscheibe uebrig sind
			// wird die Bewertung der Moves abgebrochen
			if (thinkTime - ((System.nanoTime() - startTime) / 1000000) < 5) {
				if (this.commandLine)
					System.out.println("Zeitscheibe abgelaufen");
				break;
			}

			// Rating für aktuellen Move berechnen. Legale Moves werden nach
			// ihrem Rating sortiert in einer TreeMap gespeichert
			rating = calculateValue(generatedMoves.get(i), false);
			if (rating == -1)
				continue;

			// Existiert schon ein Eintrag für das aktuelle Rating wird der Move
			// an diese Liste gehaengt
			// Existiert noch kein Eintrag wird eine neue Liste erstellt und in
			// der TreeMap gespeichert
			if (!ratedMoves.containsKey(rating)) {
				possibleMoves = new ArrayList<Integer>();
			} else {
				possibleMoves = ratedMoves.get(rating);
			}
			possibleMoves.add(i);
			ratedMoves.put(rating, possibleMoves);
		}

		// Aus den Moves mit dem besten Rating einen per Zufall auswaehlen
		ArrayList<Integer> bestMoves = ratedMoves.get(ratedMoves.lastKey());
		int selectedMove = r.nextInt(bestMoves.size());

		// Move auf KI-internem Board übernehmen
		this.b = doMove(generatedMoves.get(bestMoves.get(selectedMove)), this.b);

		long t2 = (System.nanoTime() - startTime) / 1000000;
		if (this.commandLine)
			System.out.println("Zeit fuer Attackermove: " + t2 + " ms");
		return generatedMoves.get(bestMoves.get(selectedMove));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhhannover.inform.hnefatafl.vorgaben.MoveStrategy#calculateDefenderMove
	 * (de.fhhannover.inform.hnefatafl.vorgaben.Move, int)
	 */

	@Override
	public de.fhhannover.inform.hnefatafl.vorgaben.Move calculateDefenderMove(
			de.fhhannover.inform.hnefatafl.vorgaben.Move lastMove, int thinkTime) {
		// Startzeit festhalten
		long startTime = System.nanoTime();

		// Lokale Variablen
		int rating;
		ArrayList<Integer> possibleMoves;
		TreeMap<Integer, ArrayList<Integer>> ratedMoves = new TreeMap<Integer, ArrayList<Integer>>();

		// Uebergebenen Move auf KI-internem Board uebernehmen
		this.b = doMove(lastMove, this.b);

		// ArrayList mit allen erlauben Moves generieren
		ArrayList<Move> generatedMoves = generateMoves(BoardContent.DEFENDER);
		generatedMoves.addAll(generateMoves(BoardContent.KING));

		// Moves nach Rating sortiert in TreeMap speichern
		for (int i = 0; i < generatedMoves.size(); i++) {

			// Wenn nur noch weniger als 5ms der Zeitscheibe uebrig sind
			// wird die Bewertung der Moves abgebrochen
			if (thinkTime - ((System.nanoTime() - startTime) / 1000000) < 5) {
				if (this.commandLine)
					System.out.println("Zeitscheibe abgelaufen");
				break;
			}

			// Rating für aktuellen Move berechnen. Legale Moves werden nach
			// ihrem Rating sortiert in einer TreeMap gespeichert
			rating = calculateValue(generatedMoves.get(i), true);
			if (rating == -1)
				continue;

			// Existiert schon ein Eintrag für das aktuelle Rating wird der Move
			// an diese Liste gehaengt
			// Existiert noch kein Eintrag wird eine neue Liste erstellt und in
			// der TreeMap gespeichert
			if (!ratedMoves.containsKey(rating)) {
				possibleMoves = new ArrayList<Integer>();
			} else {
				possibleMoves = ratedMoves.get(rating);
			}
			possibleMoves.add(i);
			ratedMoves.put(rating, possibleMoves);
		}

		// Aus den Moves mit dem besten Rating einen per Zufall auswählen
		ArrayList<Integer> bestMoves = ratedMoves.get(ratedMoves.lastKey());
		int selectedMove = r.nextInt(bestMoves.size());

		// Move auf KI-internem Board uebernehmen
		this.b = doMove(generatedMoves.get(bestMoves.get(selectedMove)), this.b);

		long t2 = (System.nanoTime() - startTime) / 1000000;
		if (this.commandLine)
			System.out.println("Zeit fuer Defendermove: " + t2 + " ms");
		return generatedMoves.get(bestMoves.get(selectedMove));
	}
}
