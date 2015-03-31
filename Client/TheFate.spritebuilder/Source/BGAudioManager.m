//
//  BGAudioManager.m
//  TheFate
//
//  Created by Killua Liu on 3/22/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGAudioManager.h"
#import "BGGameSetting.h"

@interface BGAudioManager ()

@property (nonatomic, strong) OALSimpleAudio *simpleAudio;

@end

@implementation BGAudioManager

static BGAudioManager *instanceOfAudioManager = nil;

+ (id)sharedAudioManager
{
    if (!instanceOfAudioManager) {
        instanceOfAudioManager = [[self alloc] init];
    }
	return instanceOfAudioManager;
}

- (id)init
{
    if (self = [super init]) {
        self.simpleAudio = [OALSimpleAudio sharedInstance];
//        [_simpleAudio preloadBg:@"Sounds/MenuTheme.mp3"];
//        [_simpleAudio preloadBg:@"Sounds/GameTheme.mp3"];
        
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        self.musicVolume = [userDefaults floatForKey:kMusicVolume];
        self.soundVolume = [userDefaults floatForKey:kSoundVolume];
        self.voiceVolume = [userDefaults floatForKey:kVoiceVolume];
    }
    return self;
}

- (void)setMusicVolume:(float)musicVolume
{
    _simpleAudio.bgVolume = musicVolume;
}

- (void)playMenuTheme
{
//    [_simpleAudio playBg:@"Sounds/MenuTheme.mp3" loop:YES];
}

- (void)playGameTheme
{
//    [_simpleAudio playBg:@"Sounds/GameTheme.mp3" loop:YES];
}

- (void)playSoundEffect:(NSString *)soundName
{
    _simpleAudio.effectsVolume = _soundVolume;
    [_simpleAudio playEffect:soundName];
}

- (void)playVoice:(NSString *)soundName
{
    _simpleAudio.effectsVolume = _voiceVolume;
    [_simpleAudio playEffect:soundName];
//    [_simpleAudio playEffect:soundName volume:_voiceVolume pitch:1.2 pan:0 loop:NO];
}

#pragma mark - Play Method
- (void)playStartTurn
{
#if __CC_PLATFORM_IOS
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
#elif __CC_PLATFORM_ANDROID
    
#endif
}

- (void)playButtonClick
{
    [self playSoundEffect:@"Sounds/ButtonClick.wav"];
}

- (void)playBigButtonClick
{
    [self playSoundEffect:@"Sounds/BigButtonClick.wav"];
}

- (void)playStoreClick
{
    [self playSoundEffect:@"Sounds/StoreClick.wav"];
}

- (void)playCardUse
{
    [self playSoundEffect:@"Sounds/CardUse.wav"];
}

- (void)playCardEquip
{
    [self playSoundEffect:@"Sounds/CardEquip.wav"];
}

- (void)playTargetClick
{
    [self playSoundEffect:@"Sounds/TargetClick.wav"];
}

- (void)playHeroSkillClick
{
    [self playSoundEffect:@"Sounds/HeroSkillClick.wav"];
}

- (void)playEquipmentClick
{
    [self playSoundEffect:@"Sounds/EquipmentClick.wav"];
}

- (void)playDamage
{
    [self playSoundEffect:@"Sounds/Damage.wav"];
}

- (void)playRestoration
{
    [self playSoundEffect:@"Sounds/Restoration.wav"];
}

- (void)playSPObtain
{
    [self playSoundEffect:@"Sounds/SPObtain.wav"];
}

- (void)playMessage
{
    [self playSoundEffect:@"Sounds/Message.wav"];
}

- (void)playVictory
{
    [self playSoundEffect:@"Sounds/Victory.wav"];
}

- (void)playFailure
{
    [self playSoundEffect:@"Sounds/Failure.wav"];
}

- (void)playGameExit
{
    [self playSoundEffect:@"Sounds/GameExit.wav"];
}

- (void)playJoinRoom
{
    [self playSoundEffect:@"Sounds/JoinRoom.wav"];
}

- (void)playReadyTick
{
    [self playSoundEffect:@"Sounds/ReadyTick.wav"];
}

- (void)playReadyMark
{
    [self playSoundEffect:@"Sounds/ReadyMark.wav"];
}

- (void)playNoChatting
{
    [self playSoundEffect:@"Sounds/NoChatting.wav"];
}

- (void)playGoldCoin
{
    [self playSoundEffect:@"Sounds/GoldCoin.wav"];
}

- (void)playRecharge
{
    [self playSoundEffect:@"Sounds/Recharge.wav"];
}

- (void)playFirstBlood
{
    [self playSoundEffect:@"Sounds/FirstBlood.wav"];
}

- (void)playDoubleKill
{
    [self playSoundEffect:@"Sounds/DoubleKill.wav"];
}

- (void)playTripleKill
{
    [self playSoundEffect:@"Sounds/TripleKill.wav"];
}

@end
