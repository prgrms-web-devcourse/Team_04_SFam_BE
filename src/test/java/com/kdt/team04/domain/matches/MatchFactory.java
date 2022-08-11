package com.kdt.team04.domain.matches;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.kdt.team04.domain.matches.match.model.MatchStatus;
import com.kdt.team04.domain.matches.match.model.MatchType;
import com.kdt.team04.domain.matches.match.model.entity.Match;
import com.kdt.team04.domain.matches.proposal.entity.MatchChat;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposal;
import com.kdt.team04.domain.matches.proposal.entity.MatchProposalStatus;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.teams.teammember.model.entity.TeamMember;
import com.kdt.team04.domain.user.entity.User;

public class MatchFactory {

	public static User getUser(String name) {
		return User.builder()
			.password("1234")
			.username(name)
			.nickname(name + "Nik")
			.build();
	}

	public static Team getTeamSoccer(String name, User user) {
		return Team.builder()
			.name(name + "-t")
			.description("we are team " + name)
			.sportsCategory(SportsCategory.SOCCER)
			.leader(user)
			.build();
	}

	public static TeamMember getTeamMember(Team team, User user, TeamMemberRole role) {
		return new TeamMember(team, user, role);
	}

	public static List<TeamMember> getTeamMembers(Team team, User leader, User... members) {
		List<TeamMember> teamMembers = new ArrayList<>();
		teamMembers.add(getTeamMember(team, leader, TeamMemberRole.LEADER));
		for (User member : members) {
			teamMembers.add(getTeamMember(team, member, TeamMemberRole.MEMBER));
		}
		return teamMembers;
	}

	public static Match getTeamMatchSoccer(int participants, MatchStatus status, User user, Team team) {
		return Match.builder()
			.title("덤벼라!")
			.sportsCategory(SportsCategory.SOCCER)
			.matchType(MatchType.TEAM_MATCH)
			.matchDate(LocalDate.now())
			.participants(participants)
			.status(status)
			.user(user)
			.team(team)
			.build();
	}

	public static Match getIndividualMatchSoccer(MatchStatus status, User user) {
		return Match.builder()
			.title("덤벼라!")
			.sportsCategory(SportsCategory.SOCCER)
			.matchType(MatchType.INDIVIDUAL_MATCH)
			.matchDate(LocalDate.now())
			.participants(1)
			.status(status)
			.user(user)
			.build();
	}

	public static MatchProposal getTeamProposal(
		Match match,
		User proposer,
		Team proposerTeam,
		MatchProposalStatus status
	) {
		return MatchProposal.builder()
			.match(match)
			.content("덤벼라!")
			.user(proposer)
			.team(proposerTeam)
			.status(status)
			.build();
	}

	public static MatchProposal getIndividualProposal(
		Match match,
		User proposer,
		MatchProposalStatus status
	) {
		return MatchProposal.builder()
			.match(match)
			.content("겜 하실?")
			.user(proposer)
			.status(status)
			.build();
	}

	public static MatchChat getChat(MatchProposal proposal, User user, User target, String content) {
		return MatchChat.builder()
			.proposal(proposal)
			.user(user)
			.target(target)
			.content(content)
			.chattedAt(LocalDateTime.now())
			.build();
	}
}
