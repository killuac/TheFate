//
//  BGTransitionManager.h
//  TheFate
//
//  Created by Killua Liu on 3/18/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BGTransitionManager : NSObject

+ (id)transitSceneToLeft:(NSString *)ccbFileName;
+ (id)transitSceneToRight:(NSString *)ccbFileName;
+ (id)transitSceneToUp:(NSString *)ccbFileName;
+ (id)transitSceneToDown:(NSString *)ccbFileName;

@end
