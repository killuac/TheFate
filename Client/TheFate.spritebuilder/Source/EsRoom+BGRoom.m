//
//  EsRoom+BGRoom.m
//  TheFate
//
//  Created by Killua Liu on 4/11/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "EsRoom+BGRoom.h"
#import "BGVarConstant.h"

@implementation EsRoom (BGRoom)

- (EsRoomVariable *)roomSetting
{
    return [self roomVariableByName:kVarRoomSetting];
}

- (NSString *)roomNumber
{
    return [self.roomSetting.value stringWithKey:kParamRoomNumber];
}

// Can't get EsRoom's password value
- (void)setRoomPassword:(NSString *)roomPassword
{
    [self.roomSetting.value setString:roomPassword forKey:kParamRoomPassword];
}

- (NSString *)roomPassword
{
    return [self.roomSetting.value stringWithKey:kParamRoomPassword];
}

- (void)setPlayTime:(NSUInteger)playTime
{
    [self.roomSetting.value setInt:(int32_t)playTime forKey:kParamPlayTime];
}

- (NSUInteger)playTime
{
    return [self.roomSetting.value intWithKey:kParamPlayTime];
}

- (void)setIsNoChatting:(BOOL)isNoChatting
{
    [self.roomSetting.value setBool:isNoChatting forKey:kParamIsNoChatting];
}

- (BOOL)isNoChatting
{
    return [self.roomSetting.value boolWithKey:kParamIsNoChatting];
}

@end
