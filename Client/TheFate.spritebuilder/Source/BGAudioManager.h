//
//  BGAudioManager.h
//  TheFate
//
//  Created by Killua Liu on 3/22/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BGAudioManager : NSObject

+ (id)sharedAudioManager;

@property (nonatomic) float musicVolume;
@property (nonatomic) float soundVolume;
@property (nonatomic) float voiceVolume;

- (void)playMenuTheme;
- (void)playGameTheme;
- (void)playSoundEffect:(NSString *)soundName;
- (void)playVoice:(NSString *)soundName;

- (void)playStartTurn;
- (void)playButtonClick;
- (void)playBigButtonClick;
- (void)playStoreClick;
- (void)playCardUse;
- (void)playCardEquip;
- (void)playTargetClick;
- (void)playHeroSkillClick;
- (void)playEquipmentClick;
- (void)playDamage;
- (void)playRestoration;
- (void)playSPObtain;
- (void)playMessage;
- (void)playVictory;
- (void)playFailure;
- (void)playGameExit;
- (void)playJoinRoom;
- (void)playReadyMark;
- (void)playReadyTick;
- (void)playNoChatting;
- (void)playGoldCoin;
- (void)playRecharge;
- (void)playFirstBlood;
- (void)playDoubleKill;
- (void)playTripleKill;


@end
