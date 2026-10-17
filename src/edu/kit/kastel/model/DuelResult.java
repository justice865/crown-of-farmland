package edu.kit.kastel.model;

/**
 * The record represents the occurrences and outcomes of certain events during a duel in a game.
 * @param duelOccurred indicates whether a duel took place or not
 * @param attackerWins indicates whether the attacker won the duel
 * @param stalemate indicates whether the duel resulted in a stalemate
 * @param attackerMoves indicates whether the attacker is allowed to move after the duel
 * @param attackerFlipped indicates whether the attacker's unit was flipped during the duel
 * @param defenderFlipped indicates whether the defender's unit was flipped during the duel
 * @author uqibg
 */
public record DuelResult(boolean duelOccurred, boolean attackerWins,
                         boolean stalemate, boolean attackerMoves, boolean attackerFlipped,
                         boolean defenderFlipped) {
}

