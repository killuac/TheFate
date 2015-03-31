//
//  BGHandCard.m
//  FateClient
//
//  Created by Killua Liu on 12/31/13.
//
//

#import "BGHandCard.h"
#import "BGGameScene.h"

@implementation BGHandCard

- (id)initWithCardId:(NSInteger)cardId ofPlayer:(BGPlayer *)player
{
    if (self = [super initWithCardId:cardId]) {
        _player = player;
    }
    return self;
}

+ (id)handCardWithCardId:(NSInteger)cardId ofPlayer:(BGPlayer *)player
{
    BGHandCard *card = [[BGHandCard alloc] initWithCardId:cardId ofPlayer:player];
    NSString *className = [kClassPrefix stringByAppendingString:card.cardName];
    id dynCard = [[NSClassFromString(className) alloc] initWithCardId:cardId ofPlayer:player];
    return (dynCard) ? dynCard : card;
}

+ (NSMutableArray *)handCardsByCardIds:(NSArray *)cardIds ofPlayer:(BGPlayer *)player
{
    NSMutableArray *cards = [NSMutableArray arrayWithCapacity:cardIds.count];
    for (NSNumber *cardId in cardIds) {
        [cards addObject:[BGHandCard handCardWithCardId:cardId.integerValue ofPlayer:player]];
    }
    return cards;
}

+ (NSMutableArray *)handCardIdsByCards:(NSArray *)cards ofPlayer:(BGPlayer *)player
{
    NSMutableArray *cardIds = [NSMutableArray arrayWithCapacity:cards.count];
    for (BGHandCard *card in cards) {
        [cardIds addObject:@(card.cardId)];
    }
    return cardIds;
}

- (NSString *)targetTipText
{
    return [BGUtil textWith:_targetTipText parameters:self.player.game.tipParameters];
}

- (NSString *)dispelTipText
{
    return [BGUtil textWith:_dispelTipText parameters:self.player.game.tipParameters];
}

#pragma mark - protocol implementation
- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    if (PlayingCardChakra      == self.cardEnum ||
        PlayingCardPlunderAxe  == self.cardEnum ||
        PlayingCardMysticStaff == self.cardEnum ||
        PlayingCardEaglehorn   == self.cardEnum) {
        self.player.maxTargetCount = 1;
        if (0 == self.player.targetPlayerNames.count) {
            [self.player.targetPlayerNames addObject:self.player.playerName];   // Target is self by default
        }
        return (!player.isMe);
    }
    
    return (!player.isMe && (PlayingCardWildAxe     == self.cardEnum ||
                             PlayingCardElunesArrow == self.cardEnum ||
                             PlayingCardViperRaid   == self.cardEnum ||
                             PlayingCardLagunaBlade == self.cardEnum ||
                             PlayingCardSunder      == self.cardEnum));
}

- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player
{
    return false;
}

#pragma mark - Sound
- (NSString *)soundName
{
    NSString *soundName = @"";
    if (HeroGenderMale == _player.character.heroCard.gender) {
        soundName = [NSString stringWithFormat:@"Sounds/Card/Male/%@", _cardName];
    } else {
        soundName = [NSString stringWithFormat:@"Sounds/Card/Female/%@", _cardName];
    }
    return [soundName stringByAppendingPathExtension:kFileTypeCAF];
}

- (void)playSound
{
    if (!self.player.heroSkill && !self.player.equipmentCard) {
        [[BGAudioManager sharedAudioManager] playVoice:[self soundName]];
    }
}

#pragma mark - Resolve
- (void)resolveUse
{
    
}

- (void)resolveOkay
{
    
}

@end
