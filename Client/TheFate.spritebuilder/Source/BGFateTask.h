//
//  BGFateTask.h
//  TheFate
//
//  Created by Killua Liu on 4/13/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"

@interface BGFateTask : CCSprite {
    CGPoint _touchedPos;
    NSTimeInterval _beganTimestamp;
}

@end
