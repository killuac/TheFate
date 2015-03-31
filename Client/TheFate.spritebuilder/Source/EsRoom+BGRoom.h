//
//  EsRoom+BGRoom.h
//  TheFate
//
//  Created by Killua Liu on 4/11/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "EsRoom.h"

@interface EsRoom (BGRoom)

@property (nonatomic, strong, readonly) EsRoomVariable *roomSetting;

@property (nonatomic, copy, readonly) NSString* roomNumber;
@property (nonatomic, copy) NSString *roomPassword;

@property (nonatomic) NSUInteger playTime;
@property (nonatomic) BOOL isNoChatting;

@end
