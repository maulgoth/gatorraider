package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Attacker;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;
import java.util.ArrayList;

public final class StudentAttackerController implements AttackerController
{

	private boolean pillMode = true;
	private boolean chillMode = false;
	private boolean killMode = false;

	private Attacker player;
	private Node playerLocation;
	private Node goal;
	private List<Defender> ghostList;
	private List<Node> vulGhosts;
	private List<Node> availPillList;
	private List<Node> powerPillList;
	private List<Node> neighbors;
//	private int currentDirection;


	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue) {
		// 0 - UP | 1 - RIGHT | 2 - DOWN | 3 - LEFT

		int action;

		// LOGIC:
		// Three major modes: pillMode, chillMode, killMode
		// pillMode: Head to powerPill, avoid ghosts, take path of availPillList.
		// 			 If powerPills gone, take path of availPillList.
		// chillMode: When neighboring powerPill, reverse until ghost arrives.
		// killMode: Chase blinking ghosts, avoid normal ghosts.

		// UPDATE Ghosts and Player (Reduce verbosity)
		ghostList = game.getDefenders();
		player = game.getAttacker();
		playerLocation = player.getLocation();
		powerPillList = game.getPowerPillList();
		neighbors = player.getLocation().getNeighbors();
//		currentDirection = player.getDirection();



		if (pillMode) {

			// If powerPills available, head to them
			if (powerPillList.size() > 0) {
				goal = player.getTargetNode(powerPillList, true);

				// Check if neighbor is
				for (Node p : powerPillList) {
//					if (playerLocation.getNeighbor(currentDirection) == p) {
					if (playerLocation.getPathDistance(p) < 5) {
						pillMode = false;
						System.out.println("Entering CHILL MODE!");
						chillMode = true;
						break;
					}
				}
			}

			action = player.getNextDir(goal, true);
			return action;
		}

		else if (chillMode) {
			// If in chillMode, reverse direction every turn until
			// Ghost is within 5 spaces
			for (Defender d : ghostList) {
				int distancePlayerToGhost = playerLocation.getPathDistance(d.getLocation());
				System.out.println(distancePlayerToGhost);
				if (distancePlayerToGhost < 10 && distancePlayerToGhost > 5) {
					chillMode = false;
					System.out.println("Entering KILL MODE!");
					killMode = true;
					break;
				}
			}
			action = player.getReverse();
			return action;

		}

		else if (killMode) {
			for (Defender d : ghostList) {
				if (d.isVulnerable()) {
					vulGhosts.add(d.getLocation());
				}
			}
			action = player.getNextDir(player.getTargetNode(vulGhosts, true), true);
			return action;
		}






		// DEFAULT ACTION ---------------------------------------------------------------------------------------------
		action = 0;
		return action;
	}
}