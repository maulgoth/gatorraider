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
		powerPillList = game.getPowerPillList();
		System.out.println(powerPillList);

		if (pillMode) {
			if (powerPillList.size() > 0) {
				goal = player.getTargetNode(powerPillList, true);
				action = player.getNextDir(goal, true);
				return action;
			}
		}






		// DEFAULT ACTION ---------------------------------------------------------------------------------------------
		action = 0;
		return action;
	}
}