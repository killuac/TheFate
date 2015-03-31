//
//  BGPlayingButton.m
//  TheFate
//
//  Created by Killua Liu on 3/22/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPlayingButton.h"
#import "BGGameScene.h"

@implementation BGPlayingButton

- (BGGameScene *)game
{
    return self.player.game;
}

- (BGPlayer *)player
{
    return (BGPlayer *)self.parent;
}

- (void)setOkayEnabled:(BOOL)isEnabled
{
    CCButton *okayButton = (CCButton *)[self getChildByName:kButtonNameOkay recursively:NO];
    okayButton.enabled = isEnabled;
}

- (CCButton *)createButtonWithImageName:(NSString *)imageName
{
    return [self createButtonWithImageName:imageName disabledImageName:nil];
}

- (CCButton *)createButtonWithImageName:(NSString *)imageName disabledImageName:(NSString *)disImageName
{
    CCSpriteFrame *spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:imageName];
    CCSpriteFrame *disSpriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:disImageName];
    CCButton *button = [CCButton buttonWithTitle:@""
                                     spriteFrame:spriteFrame
                          highlightedSpriteFrame:nil
                             disabledSpriteFrame:disSpriteFrame];
    [button setTarget:self selector:@selector(buttonTouched:)];
    return button;
}

- (void)addOkayButtonWithEnabled:(BOOL)isEnabled
{
    CCButton *okayButton = [self createButtonWithImageName:kImageOkay disabledImageName:kImageOkayDisabled];
    okayButton.enabled = isEnabled;
    [self addChild:okayButton z:0 name:kButtonNameOkay];
}

- (void)addOkayAndCancelButtonWithOkayEnabled:(BOOL)isEnabled
{
    self.spacing = PADDING_TWO_BUTTONS;
    [self addOkayButtonWithEnabled:isEnabled];
    CCButton *cancelButton = [self createButtonWithImageName:kImageCancel disabledImageName:kImageCancelDisabled];
    [self addChild:cancelButton z:0 name:kButtonNameCancel];
}

- (void)addOkayAndDiscardButton
{
    self.spacing = PADDING_TWO_BUTTONS;
    [self addOkayButtonWithEnabled:NO];
    CCButton *discardButton = [self createButtonWithImageName:kImageDiscard disabledImageName:kImageDiscardDisabled];
    [self addChild:discardButton z:0 name:kButtonNameDiscard];
}

- (void)addButtonWithName:(NSString *)name imageName:(NSString *)imageName
{
    CCButton *button = [self createButtonWithImageName:imageName];
    [self addChild:button z:0 name:name];
}

- (void)addColorButtons
{
    self.spacing = PADDING_TWO_BUTTONS;
    [self addButtonWithName:kButtonNameRed imageName:kImageButtonRed];
    [self addButtonWithName:kButtonNameBlack imageName:kImageButtonBlack];
}

- (void)addSuitsButtons
{
    self.spacing = PADDING_SUITS_BUTTON;
    [self addButtonWithName:kButtonNameSpades imageName:kImageButtonSpades];
    [self addButtonWithName:kButtonNameHearts imageName:kImageButtonHearts];
    [self addButtonWithName:kButtonNameClubs imageName:kImageButtonClubs];
    [self addButtonWithName:kButtonNameRed imageName:kImageButtonDiamonds];
}

#pragma mark - Button selector
- (void)buttonTouched:(CCButton *)sender
{
    if ([sender.name isEqualToString:kButtonNameOkay]) {
        [self okayTouched];
    }
    else if ([sender.name isEqualToString:kButtonNameCancel]) {
        [self.player chooseCancel];
    }
    else if ([sender.name isEqualToString:kButtonNameDiscard]) {
        [self.player startDiscard];
    }
    else if ([sender.name isEqualToString:kButtonNameRed]) {
        [self.player chooseColorWithColor:CardColorRed];
    }
    else if ([sender.name isEqualToString:kButtonNameBlack]) {
        [self.player chooseColorWithColor:CardColorBlack];
    }
    else if ([sender.name isEqualToString:kButtonNameSpades]) {
        [self.player chooseSuitsWithSuits:CardSuitsSpades];
    }
    else if ([sender.name isEqualToString:kButtonNameHearts]) {
        [self.player chooseSuitsWithSuits:CardSuitsHearts];
    }
    else if ([sender.name isEqualToString:kButtonNameClubs]) {
        [self.player chooseSuitsWithSuits:CardSuitsClubs];
    }
    else if ([sender.name isEqualToString:kButtonNameDiamonds]) {
        [self.player chooseSuitsWithSuits:CardSuitsDiamonds];
    }
    
    if (self.player.isNeedTargetAgain) {
        if (GameStatePlaying == self.game.state) {
            [self addOptionsAndTextPrompt];
        }
    } else {
        [self.player resetAndRemovePlayingNodes];
    }
    
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

/*
 * Touch okay menu item:
 * 1. Use hand card.
 * 2. Use hero skill and/or hand card.
 */
- (void)okayTouched
{
    switch (self.game.state) {
        case GameStatePlaying:
            if (ActionChooseTargetPlayer == self.game.action) {
                [self.player chooseTargetPlayer];
            }
            else {
                if (self.player.selectedCardCount > 0) {
                    [self.player playHandCard];
                } else if (self.player.heroSkill) {
                    [self.player useHeroSkill];
                } else if (self.player.equipmentCard) {
                    [self.player useEquipment];
                }
            }
            break;
            
        case GameStateTargetChoosing:
            [self.player chooseTargetPlayer];
            break;
            
        case GameStateDeathResolving:
            if (ActionChooseDrawCardOrViewRole == self.game.action) {
                [self.player chooseViewPlayerRole];
            } else {
                [self defaultOkay];
            }
            break;
            
        default:
            [self defaultOkay];
            break;
    }
}

- (void)defaultOkay
{
    if (self.player.selectedCardCount > 0) {
        [self.player playHandCard];
    } else {
        [self.player chooseOkay];
    }
}

- (void)addOptionsAndTextPrompt
{
    [self.player removePlayingButtons];
    [self addOkayAndCancelButtonWithOkayEnabled:YES];
    for (CCButton *button in self.children) {
        [button setTarget:self selector:@selector(chooseLeftOrRight:)];
    }
    
    [self.player addTextPromptForTriggering];
}

- (void)chooseLeftOrRight:(CCButton *)sender
{
    NSUInteger index = [self.player.game.alivePlayers indexOfObject:self.player.targetPlayer];
    NSUInteger startIdx = ([sender.name isEqual:kButtonNameOkay]) ? index : 1;
    
    [self.player updateTargetPlayerNamesFromIndex:startIdx];
    [self.player showTargetLinePath];
    
    [self.player resetAndRemovePlayingNodes];
    if (GameStatePlaying == self.game.state) {
        [[BGClient sharedClient] sendUseHandCardRequest];
    } else if (GameStateTargetChoosing == self.game.state) {
        [[BGClient sharedClient] sendChoseTargetPlayerRequest];
    }
    
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

@end