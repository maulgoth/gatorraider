package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.ArrayList;
import java.util.List;

public final class StudentAttackerControllerBACKUP implements AttackerController
{
	private boolean gameStart = true;
	private int livesRemaining;
	private int powerPillCount;
	private List<Node> powerPillList = new ArrayList<>();
	private Node nearestPowerPill;
	private int goalDistance = 123456789;
	private Node goalNode;
	private List<Node> goalPath = null;
	private int nextMove;
	private boolean goalPathBool = true;
	private boolean enRoute = true;
	private int pathCounter = 0;

	private List<Defender> defenderList = new ArrayList<>();
	private List<Node> defenderLocations = new ArrayList<>();

	private boolean wildMode = false;
	private boolean pillMode = false;
	private boolean chillMode = false;
	private int chillBeastCount = 0;
	private Defender defTarget = null;
	private boolean chillSwitch = false;
	private int chillDirection;



	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue)
	{
		// 0 - UP | 1 - RIGHT | 2 - DOWN | 3 - LEFT |

		int action;

		// HOUSEKEEPING -----------------------------------------------------------------------------------------------

		if (gameStart)
		{
			System.out.println("GAMESTART IS TRUE!");

			// LIFE COUNTER:
			livesRemaining = game.getLivesRemaining();
			System.out.println(livesRemaining + " Lives Remaining.");

			// DEFENDER LIST
			defenderList = game.getDefenders();
			System.out.println("DEFENDER LIST: " + defenderList);

			// DEFENDER LOCATION INIT:
			System.out.print("DEF LOCS: ");
			int initIndex = 0;
			for (Defender d : defenderList)
			{
				defenderLocations.add(d.getLocation());
				System.out.print(" D-" + initIndex + ": " + d.getLocation() + " //");
			}
			System.out.println();
			gameStart = false;
			System.out.println("SET GAMESTART TO FALSE!!! DID U HEAR ME");
		}
		else
		{

			// URGENT! FIND IF HEADED TOWARD A DEFENDER
			

			// LIFE COUNTER:
			livesRemaining = game.getLivesRemaining();
			System.out.println(livesRemaining + " Lives Remaining.");

			// DEFENDER LIST
			defenderList = game.getDefenders();
//			System.out.println("DEFENDER LIST:" + defenderList);

			// DEFENDER LOCATION SET:
			System.out.print("DEF LOCS: ");
			int index = 0;
			for (Defender d : defenderList)
			{
				defenderLocations.set(index,d.getLocation());
				System.out.print(" D-" + index + ": " + d.getLocation() + " //");
				index++;
			}
			System.out.println();

		}

		// POWERPILL LOGIC --------------------------------------------------------------------------------------------
		if (goalPathBool)
		{
			powerPillCount = game.getPowerPillList().size();
			powerPillList = game.getPowerPillList();
			System.out.println("Power Pill Count: " + powerPillCount);
//			System.out.println("Power Pill List: " + powerPillList);

			for (int i = 0; i < powerPillList.size(); i++) {
				int x = game.getAttacker().getLocation().getPathDistance(powerPillList.get(i));
				System.out.print("Pill(" + i +"): " + x + " powerPill: " + powerPillList.get(i) + ". ");
				if (x < goalDistance) {
					goalDistance = x;
					System.out.println(" goalDistance: " + goalDistance);
					nearestPowerPill = powerPillList.get(i);
				}
			}

			System.out.println("Nearest Power Pill: " + nearestPowerPill + " is " + goalDistance + " spaces away.");
		}

		// NEIGHBORS ---------------------------------------------------------------------------------------------

		List<Node> neighbors = game.getAttacker().getLocation().getNeighbors();
		System.out.println("NEIGHBORS: " + neighbors);

//		for (Node n : neighbors)
//		{
//			if (n.isPowerPill())
//			{
//				for
//				if (game.getAttacker().getPathTo(d)) // IM HERE ----------------------------------------------------------------------------------------------------------------- <
//			}
//		}


		// GOAL PATH --------------------------------------------------------------------------------------------------
		if (!pillMode && !wildMode)
		{

			if (goalPathBool)
			{
				goalPath = game.getAttacker().getPathTo(nearestPowerPill);
				pathCounter = goalPath.size() - 1;
				goalPathBool = false;
				goalDistance = 123456789;
			}

			System.out.println("GOAL PATH! Size: " + goalPath.size());

			// GOAL PATH EXECUTER ---------------------------------------------------------------------
			if (pathCounter > 0)
			{
				nextMove = game.getAttacker().getNextDir(goalPath.get(0), true);
				System.out.println("Next Move: " + nextMove);
//				System.out.print("gps: " + goalPath.size() + " ");
				goalPath.remove(0);
//				System.out.print("Goal Path: " + goalPath.size());
				pathCounter--;
				action = nextMove;
				System.out.println("------------------------------------------------------------------------PATH");
				return action;
			}

			// GOAL PATH OVER CHILLLLLL --------------------------------------------------------------------------
			if (pathCounter == 0) {
				chillMode = true;
				System.out.println("CHILL MODE ON!");
			}

			if (chillMode) {
				for (Defender d : defenderList) {
					if (d.getLairTime() == 0) {
//						System.out.println("Lair time: " + d.getLairTime());
						int x = game.getAttacker().getPathTo(d.getLocation()).size();
//						System.out.println("Ghosty " + d + " is " + x + " spots away.");
						if (x < 10) {
							System.out.println("HEADED TO THE PILL BABY!");
							pillMode = true;
							chillMode = false;
							chillBeastCount=4;
							action = game.getAttacker().getNextDir(nearestPowerPill, true);
							System.out.println("------------------------------------------------------------------------CHILL TO PILL");
							return action;
						}
					}
				}

				action = game.getAttacker().getReverse();
				System.out.println("------------------------------------------------------------------------CHILL REV");
				return action;
			}
		}

		// WILLLLLLLLLLLLD MOOOOOOOOOOOOOOOOOOOOOOODE -----------------------------------------------------------------
		if (pillMode)
		{
			for (Defender d : defenderList)
			{
				if (d.isVulnerable())
				{
					pillMode = false;
					System.out.println("EXITING PILL MODE");
					wildMode = true;
					System.out.println("ENTERING WILD MODE");
					action = game.getAttacker().getNextDir(game.getAttacker().getTargetNode(defenderLocations,true),true);
					System.out.println("------------------------------------------------------------------------PILL TO WILD");
					return action;
				}
			}
			action = game.getAttacker().getNextDir(nearestPowerPill, true);
			System.out.println("------------------------------------------------------------------------PILL");
			return action;
		}

		if (wildMode)
		{
			List<Defender> vulDefs = new ArrayList<>();
			for (Defender d : defenderList)
			{
				if (d.isVulnerable())
				{
					vulDefs.add(d);
				}
			}

			List<Node> vulDefLocs = new ArrayList<>();
			for (Defender d : vulDefs)
			{
				vulDefLocs.add(d.getLocation());
			}

			chillBeastCount = vulDefs.size();
			if (chillBeastCount == 0)
			{
				wildMode = false;
				goalPathBool = true;
				System.out.println("WILD MODE CANCELLED, AND GOAL PATH BOOL IS TRUE BABY");
				action = 0;
				System.out.println("------------------------------------------------------------------------WILDOUTBACKTONORMAL");
				return action;
			}

			Node nearestVulDef = game.getAttacker().getTargetNode(vulDefLocs,true);
			action = game.getAttacker().getNextDir(nearestVulDef, true);
			System.out.println("------------------------------------------------------------------------WILD");
			return action;
		}




		// DEFAULT ACTION ---------------------------------------------------------------------------------------------
		action = 0;
		return action;

		//Chooses a random LEGAL action if required.
//		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
//		if (possibleDirs.size() != 0)
//			action = possibleDirs.get(Game.rng.nextInt(possibleDirs.size()));
//		else
//			action = -1;
	}
}