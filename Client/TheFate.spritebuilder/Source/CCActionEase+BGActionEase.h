//
//  CCActionEase+BGActionEase.h
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCActionEase.h"

@interface CCActionEase (BGActionEase)

@end


#pragma mark -
#pragma mark Ease Exponential

/** CCEase Exponential In
 */
@interface CCActionEaseExponentialIn : CCActionEase <NSCopying>

@end

/** Ease Exponential Out
 */
@interface CCActionEaseExponentialOut : CCActionEase <NSCopying>

@end

/** Ease Exponential InOut
 */
@interface CCActionEaseExponentialInOut : CCActionEase <NSCopying>

@end