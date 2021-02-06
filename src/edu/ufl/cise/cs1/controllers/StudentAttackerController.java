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
	private List<Node> availPillList;
	private List<Node> powerPillList;
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
		availPillList = game.getPillList();

		if (pillMode) {
			// If powerPills available, head to them
			if (powerPillList.size() > 0) {
				goal = player.getTargetNode(powerPillList, true);

				// Check if neighbor is
				for (Node p : powerPillList) {
					if (playerLocation.getPathDistance(p) < 4) {
						pillMode = false;
						chillMode = true;
						break;
					}
				}
			}
			// Otherwise, head to pills!
			else
				goal = player.getTargetNode(availPillList, true);

			// If non-vulnerable ghost is close, RUN!
			for (Defender d : ghostList) {
				if (!d.isVulnerable()) {
					int distancePlayerToGhost = playerLocation.getPathDistance(d.getLocation());
					if (distancePlayerToGhost < 10 && distancePlayerToGhost > 0) {
						action = player.getNextDir(d.getLocation(), false);
						return action;
					}
				}
			}
			action = player.getNextDir(goal, true);
			return action;
		}

		if (chillMode) {
			// If in chillMode, reverse direction every turn until
			// Ghost is within 5 spaces
			for (Defender d : ghostList) {
				int distancePlayerToGhost = playerLocation.getPathDistance(d.getLocation());
				if (distancePlayerToGhost < 10 && distancePlayerToGhost > 5) {
					chillMode = false;
					break;
				}
			}
			action = player.getReverse();
			return action;
		}

		if (killMode) {
			// Build list of vulnerable Ghosts
			List<Node> ghostLocations = new ArrayList<>();
			int index = 0;
			for (Defender d : ghostList) {
				if (d.isVulnerable())
					ghostLocations.add(d.getLocation());
				index++;
			}

			// If no vulnerable ghosts left in killMode, exit
			if (ghostLocations.size() == 0) {
				killMode = false;
				pillMode = true;
//				System.out.println("Back to the PILLS! (xxx)");
				action = player.getNextDir(goal, true);
				return action;
			}

			// If non-vulnerable ghost is close, RUN!
			for (Defender d : ghostList) {
				if (!d.isVulnerable()) {
					int distancePlayerToGhost = playerLocation.getPathDistance(d.getLocation());
					if (distancePlayerToGhost < 10 && distancePlayerToGhost > 0) {
						action = player.getNextDir(d.getLocation(), false);
						return action;
					}
				}
			}

			// killMode action is finding vulnerable ghosts and chomping them:
			action = player.getNextDir(player.getTargetNode(ghostLocations, true), true);
			return action;
		}

		// DEFAULT ACTION: ===============================================================================
		// If neither pillMode, chillMode, or killMode, pursue the PILL:
		for (Defender d : ghostList) {
			if (d.isVulnerable())
				killMode = true;
		}
		action = player.getNextDir(goal, true);
		return action;
	}
}
