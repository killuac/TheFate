//
//  BGCardPopup.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGCardPopup : CCSprite {
    CCLabelTTF *_titleLabel;
    BGLayoutBox *_upLayoutBox;      // 放手牌
    BGLayoutBox *_downLayoutBox;    // 放装备
}

@property (nonatomic, strong, readonly) CCLabelTTF *titleLabel;
@property (nonatomic, strong) BGLayoutBox *upLayoutBox;
@property (nonatomic, strong) BGLayoutBox *downLayoutBox;

@property (nonatomic, strong, readonly) NSArray *cardButtons;

@end
