//
//  BGShadowWave.m
//  FateClient
//
//  Created by Killua Liu on 12/28/13.
//
//

#import "BGShadowWave.h"
#import "BGPlayer.h"

@implementation BGShadowWave

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (player.character.healthPoint < player.character.hpLimit ||
            [self.player.targetPlayerNames containsObject:player.playerName]);
}

- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player
{
    return YES;
}

- (void)checkHandCardEnablement
{    
    if (self.player.targetPlayerNames.count <= 1) {
        [self.player.handArea disableAllHandCardsWithDarkColor];
    }
    else if (self.player.handArea.selectedCards.count > 0) {
        BGHandCard *selCard = self.player.handArea.selectedCards.lastObject;
        NSMutableArray *cardIds = [NSMutableArray array];
        for (BGHandCard *card in self.player.handArea.handCards) {
            // Available card must be same suits
            if (card.isSelected || (card.cardSuits == selCard.cardSuits))
                [cardIds addObject:@(card.cardId)];
        }
        [self.player enableHandCardWithCardIds:cardIds];
    }
    else {
        [self.player.handArea enableAllHandCards];
    }
}

- (NSString *)dispelTipText
{
    BGPlayer *playerA = self.player.targetPlayers.firstObject;
    BGPlayer *playerB = self.player.targetPlayers.lastObject;
    NSArray *parameters = @[self.player.heroName, playerA.heroName, playerB.heroName];
    return [BGUtil textWith:_dispelTipText parameters:parameters];
}

@end
