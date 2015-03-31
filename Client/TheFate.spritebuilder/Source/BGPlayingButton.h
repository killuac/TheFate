//
//  BGPlayingButton.h
//  TheFate
//
//  Created by Killua Liu on 3/22/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

#define kButtonNameOkay     @"okay"
#define kButtonNameCancel   @"cancel"
#define kButtonNameDiscard  @"discard"
#define kButtonNameRed      @"red"
#define kButtonNameBlack    @"black"
#define kButtonNameSpades   @"spades"
#define kButtonNameHearts   @"hearts"
#define kButtonNameClubs    @"clubs"
#define kButtonNameDiamonds @"diamonds"

@interface BGPlayingButton : CCLayoutBox

- (void)setOkayEnabled:(BOOL)isEnabled;

- (void)addOkayButtonWithEnabled:(BOOL)isEnabled;
- (void)addOkayAndCancelButtonWithOkayEnabled:(BOOL)isEnabled;
- (void)addOkayAndDiscardButton;
- (void)addColorButtons;
- (void)addSuitsButtons;

- (void)addOptionsAndTextPrompt;
- (void)chooseLeftOrRight:(CCButton *)sender;

@end
