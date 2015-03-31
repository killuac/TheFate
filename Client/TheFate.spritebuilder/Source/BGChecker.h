//
//  BGChecker.h
//  FateClient
//
//  Created by Killua Liu on 12/17/13.
//
//

#import <Foundation/Foundation.h>

@class BGPlayer;

@protocol BGChecker <NSObject>

- (BOOL)checkPlayerEnablement:(BGPlayer *)player;
- (BOOL)checkNextPlayerEnablement:(BGPlayer *)player;

@optional
- (void)checkHandCardEnablement;

@end
