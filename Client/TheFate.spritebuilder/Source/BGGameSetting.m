//
//  BGGameSetting.m
//  TheFate
//
//  Created by Killua Liu on 4/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGGameSetting.h"


@implementation BGGameSetting

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    _musicSlider.sliderValue = [userDefaults floatForKey:kMusicVolume];
    _soundSlider.sliderValue = [userDefaults floatForKey:kSoundVolume];
    _voiceSlider.sliderValue = [userDefaults floatForKey:kVoiceVolume];
    _vibrationButton.selected = [userDefaults boolForKey:kVibration];
    _aidSelectionButton.selected = [userDefaults boolForKey:kAidSelection];
    _showNickButton.selected = [userDefaults boolForKey:kShowNick];
    
    [_musicSlider addHighlightedBackground];
    [_soundSlider addHighlightedBackground];
    [_voiceSlider addHighlightedBackground];
}

- (void)valueChanged:(CCSlider *)sender
{
    sender.hlBackground.scaleX = sender.sliderValue;
    
    if ([sender.name isEqual:_musicSlider.name]) {
        [[BGAudioManager sharedAudioManager] setMusicVolume:sender.sliderValue];
    } else if ([sender.name isEqual:_soundSlider.name]) {
        [[BGAudioManager sharedAudioManager] setSoundVolume:sender.sliderValue];
    } else if ([sender.name isEqual:_voiceSlider.name]) {
        [[BGAudioManager sharedAudioManager] setVoiceVolume:sender.sliderValue];
    }
}

- (void)optionChecked:(CCButton *)sender
{
    [[BGAudioManager sharedAudioManager] playButtonClick];
}

- (void)okay:(CCButton *)sender
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setFloat:_musicSlider.sliderValue forKey:kMusicVolume];
    [userDefaults setFloat:_soundSlider.sliderValue forKey:kSoundVolume];
    [userDefaults setFloat:_voiceSlider.sliderValue forKey:kVoiceVolume];
    [userDefaults setBool:_vibrationButton.selected forKey:kVibration];
    [userDefaults setBool:_aidSelectionButton.selected forKey:kAidSelection];
    [userDefaults setBool:_showNickButton.selected forKey:kShowNick];
    [userDefaults synchronize];
    
    [super okay:sender];
}

- (void)cancel:(CCButton *)sender
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [[BGAudioManager sharedAudioManager] setMusicVolume:[userDefaults floatForKey:kMusicVolume]];
    [[BGAudioManager sharedAudioManager] setSoundVolume:[userDefaults floatForKey:kSoundVolume]];
    [[BGAudioManager sharedAudioManager] setVoiceVolume:[userDefaults floatForKey:kVoiceVolume]];
    
    [super cancel:sender];
}

@end
