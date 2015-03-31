//
//  BGGameOverCell.h
//  TheFate
//
//  Created by Killua Liu on 3/28/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGGameOverCell : CCTableViewCell {
    CCLabelTTF *_nickNameLabel;
    CCLabelTTF *_heroNameLabel;
    CCSprite *_factionSprite;
    CCLabelTTF *_aliveStatusLabel;
    CCLabelTTF *_killEnemyLabel;
    CCLabelTTF *_expLabel;
    CCLabelTTF *_addExpLabel;
}

@property (nonatomic, strong, readonly) CCLabelTTF *nickNameLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *heroNameLabel;
@property (nonatomic, strong, readonly) CCSprite *factionSprite;
@property (nonatomic, strong, readonly) CCLabelTTF *aliveStatusLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *killEnemyLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *expLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *addExpLabel;

@end
