//
//  BGCardButton.h
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGCardButton : CCButton {
    CGPoint _touchedPos;
    NSTimeInterval _beganTimestamp;
}

@property (nonatomic, strong) CCLabelTTF *heroLabel;
@property (nonatomic, strong) CCSprite *selectionHalo;

@property (nonatomic, strong) id runningSceneNode;
@property (nonatomic) NSInteger oldZOrder;
@property (nonatomic) BOOL isLongPressed;
@property (nonatomic) BOOL isHeroCard;

- (void)showInformation;

@end
