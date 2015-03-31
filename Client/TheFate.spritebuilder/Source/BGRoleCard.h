//
//  BGRoleCard.h
//  FateClient
//
//  Created by Killua Liu on 5/30/13.
//
//

#import "BGCard.h"

typedef NS_ENUM(NSInteger, BGRoleCardEnum) {
    RoleCardNull = 0,
    RoleCardSentinel,       // 近卫
    RoleCardScourge,        // 天灾
    RoleCardNeutral,        // 中立
};


@interface BGRoleCard : BGCard

@property (nonatomic, readonly) NSInteger cardEnum;
@property (nonatomic, copy, readonly) NSString *deathImageName;

@end
