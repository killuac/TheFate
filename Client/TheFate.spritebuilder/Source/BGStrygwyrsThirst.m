//
//  BGStrygwyrsThirst.m
//  FateClient
//
//  Created by Killua Liu on 12/30/13.
//
//

#import "BGStrygwyrsThirst.h"
#import "BGGameScene.h"

@implementation BGStrygwyrsThirst

- (NSString *)targetTipText
{
    if (self.player.targetPlayer.playedCardIds.count > 0) {
        return self.player.targetPlayer.firstUsedCard.targetTipText;
    }
    
    NSString *cardCount = @(self.player.targetPlayer.selectableCardCount).stringValue;
    NSArray *parameters = @[self.player.game.tipParameters.firstObject, self.player.firstUsedCard.cardText, cardCount];
    return [BGUtil textWith:_targetTipText parameters:parameters];
}

@end
