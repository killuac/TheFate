//
//  CCTouch+BGTouch.h
//  TheFate
//
//  Created by Killua Liu on 11/5/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCTouch.h"

@interface CCTouch (BGTouch)

- (CGPoint)glLocationInTouchedView;
- (CGPoint)glPreviousLocationInTouchedView;
- (CGPoint)offsetPosition;

- (BOOL)isTouchedOnNode:(CCNode*)node;

@end
