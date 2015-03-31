//
//  BGIlluminate.m
//  FateClient
//
//  Created by Killua Liu on 12/27/13.
//
//  弃3张不同花色的手牌

#import "BGIlluminate.h"
#import "BGPlayer.h"

@implementation BGIlluminate

- (void)checkHandCardEnablement
{
    NSUInteger count = self.player.targetPlayerNames.count;
    self.player.requiredTargetCount = count;
    
    if (0 == self.player.targetPlayerNames.count) {
        [self.player.handArea disableAllHandCardsWithDarkColor];
        [self.player.equipment disableAllEquipments];
    }
    else if (self.player.handArea.selectedCards.count > 0 || self.player.equipment.selectedCards.count > 0) {
        NSMutableArray *selCardSuits = [NSMutableArray arrayWithCapacity:3];
        for (BGHandCard *card in self.player.handArea.selectedCards) {
            [selCardSuits addObject:@(card.cardSuits)];
        }
        for (BGHandCard *card in self.player.equipment.selectedCards) {
            [selCardSuits addObject:@(card.cardSuits)];
        }
        
        NSMutableArray *cardIds = [NSMutableArray array];
        for (BGHandCard *card in self.player.handArea.handCards) {
            // Available card must be different suits
            if (card.isSelected || ![selCardSuits containsObject:@(card.cardSuits)])
                [cardIds addObject:@(card.cardId)];
        }
        [self.player enableHandCardWithCardIds:cardIds];
        
        for (BGHandCard *card in self.player.equipment.equippedCards) {
            // Available card must be different suits
            if (card.isSelected || ![selCardSuits containsObject:@(card.cardSuits)])
                [cardIds addObject:@(card.cardId)];
        }
        [self.player enableEquipmentWithCardIds:cardIds isUse:NO];
    }
    else {
        [self.player.handArea enableAllHandCards];
        [self.player.equipment enableAllEquipments];
    }
}

@end
