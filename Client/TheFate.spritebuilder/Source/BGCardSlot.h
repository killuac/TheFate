//
//  BGCardSlot.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"
#import "BGCardSprite.h"

@interface BGCardSlot : CCSprite {
    CCLabelTTF *_heroNameLabel;
}

@property (nonatomic, strong, readonly) CCLabelTTF *heroNameLabel;

@end
