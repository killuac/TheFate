//
//  CCActionInstant+BGActionInstant.m
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCActionInstant+BGActionInstant.h"

@implementation CCActionInstant (BGActionInstant)

@end


#pragma mark CCCallBlockO

@implementation CCActionCallBlockO

@synthesize object=_object;

+(id) actionWithBlock:(void(^)(id object))block object:(id)object
{
	return [[self alloc] initWithBlock:block object:object];
}

-(id) initWithBlock:(void(^)(id object))block object:(id)object
{
	if ((self = [super init])) {
		_block = block;
		_object = object;
	}
    
	return self;
}

-(id) copyWithZone: (NSZone*) zone
{
	CCActionInstant *copy = [[[self class] allocWithZone: zone] initWithBlock:_block];
	return copy;
}

-(void) update:(CCTime)time
{
	[self execute];
}

-(void) execute
{
	_block(_object);
}

@end