//
//  BGHistory.h
//  FateClient
//
//  Created by Killua Liu on 3/9/14.
//
//

#import <Foundation/Foundation.h>

@class BGPlayer;

@interface BGHistory : NSObject

@property (nonatomic, strong, readonly) BGPlayer *player;
@property (nonatomic, copy, readonly) NSString *heroName;
@property (nonatomic, strong, readonly) NSDate *dateTime;
@property (nonatomic, copy, readonly) NSString *text;

@property (nonatomic, readonly) BOOL isStartTurn;
@property (nonatomic, readonly) BOOL isShowTip;     // Show tip for informing player what happened

+ (id)historyOfPlayer:(BGPlayer *)player withEsObject:(EsObject *)esObj;

@end
