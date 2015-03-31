//
//  CCSprite+BGSprite.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCSprite.h"

@interface CCSprite (BGSprite)

+ (id)spriteWithImageData:(NSData *)data key:(NSString *)key;

#if __CC_PLATFORM_IOS
+ (UIImage *)getUIImageByImageNamed:(NSString *)imageName;
#endif

@end
