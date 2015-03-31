//
//  BGHandCard.h
//  FateClient
//
//  Created by Killua Liu on 12/31/13.
//
//

#import "BGPlayingCard.h"
#import "BGChecker.h"

@class BGPlayer;

@interface BGHandCard : BGPlayingCard <BGChecker>

@property (nonatomic, weak, readonly) BGPlayer *player;
@property (nonatomic, readwrite) BOOL isSelected;
@property (nonatomic, readwrite) BOOL isAnimationRan;

// Create dynamic playing card instance according to different card subclass
- (id)initWithCardId:(NSInteger)cardId ofPlayer:(BGPlayer *)player;
+ (id)handCardWithCardId:(NSInteger)cardId ofPlayer:(BGPlayer *)player;

+ (NSMutableArray *)handCardsByCardIds:(NSArray *)cardIds ofPlayer:(BGPlayer *)player;
+ (NSMutableArray *)handCardIdsByCards:(NSArray *)cards ofPlayer:(BGPlayer *)player;

- (void)playSound;

- (void)resolveUse;
- (void)resolveOkay;

@end
