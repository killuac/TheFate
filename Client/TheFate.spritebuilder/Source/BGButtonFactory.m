//
//  BGButtonFactory.m
//  TheFate
//
//  Created by Killua Liu on 3/22/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGButtonFactory.h"
#import "BGMacro.h"
#import "BGFileConstant.h"
#import "BGCardSprite.h"
#import "BGEquipSprite.h"

@implementation BGButtonFactory

#pragma mark - Button box
+ (CCLayoutBox *)createCardButtonBoxWithCards:(NSArray *)cards forTarget:(id)target
{
    CCLayoutBox *layoutBox = [CCLayoutBox node];
    layoutBox.spacing = -PLAYING_CARD_WIDTH/cards.count;
    layoutBox.anchorPoint = ccp(0.5f, 0.5f);
    NSArray *cardButtons = [self createCardButtonsWithCards:cards forTarget:target];
    for (CCButton *cardButton in cardButtons) {
        [layoutBox addChild:cardButton];
    }
    return layoutBox;
}

+ (CCLayoutBox *)createCardBackBoxWithCount:(NSUInteger)count forTarget:(id)target
{
    CCLayoutBox *layoutBox = [CCLayoutBox node];
    layoutBox.spacing = -PLAYING_CARD_WIDTH/count;
    layoutBox.anchorPoint = ccp(0.5f, 0.5f);
    for (CCSprite *cardBack in [self createCardBackButtonsWithCount:count forTarget:target]) {
        [layoutBox addChild:cardBack];
    }
    return layoutBox;
}

#pragma mark - Card Button
+ (BGCardButton *)createCardButtonWithCard:(id)card forTarget:(id)target isAvatar:(BOOL)isAvatar
{
    NSString *file, *imageName;
    if ([card isPlayingCard]) {
        file = (isAvatar) ? kCcbiEquipSprite : kCcbiCardSprite;
        imageName = (isAvatar) ? [card avatarImageName] : [card imageName];
    } else {
        file = kCcbiHeroSprite;         // Hero selection button, not hero avatar.
        imageName = [card imageName];
    }
    id sprite = [CCBReader load:file];
    [sprite setSpriteFrame:[[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:imageName]];
    
    id button = ([card isPlayingCard]) ?
        [BGCardButton buttonWithTitle:@"" spriteFrame:[sprite spriteFrame]] :
        [BGHeroButton buttonWithTitle:@"" spriteFrame:[sprite spriteFrame]];
    [(CCButton *)button setName:@([card cardId]).stringValue];
    [button setTogglesSelectedState:YES];
    [button setIsHeroCard:!isAvatar];
    if (target) [button setTarget:target selector:@selector(cardSelected:)];
    
    [sprite removeAllChildren];
    if (isAvatar) {
        [sprite avatarNameLabel].string = [card verticalText];
        [button addChild:[sprite avatarNameLabel]];
    } else {
        [sprite nameLabel].string = [card cardText];
        [button addChild:[sprite nameLabel]];
    }
    
    [button setSelectionHalo:[sprite selectionHalo]];
    [button addChild:[sprite selectionHalo]];
    
    if ([card isPlayingCard]) {
        [button setBackgroundColor:[CCColor whiteColor] forState:CCControlStateHighlighted];
        [button setBackgroundOpacity:1.0f forState:CCControlStateHighlighted];
        [button setBackgroundOpacity:1.0f forState:CCControlStateDisabled];
        
        [sprite figureLabel].string = [card figureDisplayedText];
        [sprite suitsLabel].string = [card suitsDisplayedText];
        if ([card isRedColor]) {
            [sprite figureLabel].fontColor = [sprite suitsLabel].fontColor = [CCColor redColor];
        }
        
        [button addChild:[sprite figureLabel]];
        [button addChild:[sprite suitsLabel]];
        
        if (!isAvatar) {
            [button setHeroLabel:[sprite heroLabel]];
            [button addChild:[sprite heroLabel]];
        }
    }
    
    return button;
}

+ (BGCardButton *)createCardButtonWithCard:(id)card forTarget:(id)target
{
    return [self createCardButtonWithCard:card forTarget:target isAvatar:NO];
}

+ (BGCardButton *)createEquipmentButtonWithCard:(BGPlayingCard *)card forTarget:(id)target
{
    BGCardButton *button = [self createCardButtonWithCard:card forTarget:target isAvatar:YES];
    if (target) [button setTarget:target selector:@selector(equipmentSelected:)];
    return button;
}

+ (NSArray *)createCardButtonsWithCards:(NSArray *)cards forTarget:(id)target
{
    NSMutableArray *buttons = [NSMutableArray arrayWithCapacity:cards.count];
    for (id card in cards) {
        [buttons addObject:[self createCardButtonWithCard:card forTarget:target]];
    }
    return buttons;
}

+ (NSArray *)createCardBackButtonsWithCount:(NSUInteger)count forTarget:(id)target
{
    NSMutableArray *buttons = [NSMutableArray arrayWithCapacity:count];
    for (NSUInteger i = 0; i < count; i++) {
        CCSpriteFrame *spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:kImagePlayingCardBack];
        CCButton *button = [CCButton buttonWithTitle:@"" spriteFrame:spriteFrame];
        button.name = @(i).stringValue;
        button.enabled = NO;
        [button setBackgroundColor:[CCColor whiteColor] forState:CCControlStateDisabled];
        [button setBackgroundOpacity:1.0f forState:CCControlStateDisabled];
        if (target) [button setTarget:target selector:@selector(cardBackSelected:)];
        [buttons addObject:button];
    }
    return buttons;
}

- (void)cardSelected:(CCButton *)sender
{
//    Implemented in target
}

- (void)cardBackSelected:(CCButton *)sender
{
//    Implemented in target
}

- (void)equipmentSelected:(CCButton *)sender
{
//    Implemented in target
}

@end
