//
//  CCActionInstant+BGActionInstant.h
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCActionInstant.h"

@interface CCActionInstant (BGActionInstant)

@end


/* Executes a callback using a block with a single NSObject parameter. */
@interface CCActionCallBlockO : CCActionInstant<NSCopying>
{
	void (^_block)(id object);
	id _object;
}

/** object to be passed to the block */
@property (nonatomic,retain) id object;

/** creates the action with the specified block, to be used as a callback.
 The block will be "copied".
 */
+(id) actionWithBlock:(void(^)(id object))block object:(id)object;

/** initialized the action with the specified block, to be used as a callback.
 The block will be "copied".
 */
-(id) initWithBlock:(void(^)(id object))block object:(id)object;

/** executes the callback */
-(void) execute;

@end
