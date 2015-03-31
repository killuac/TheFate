//
//  BGGameSetting.h
//  TheFate
//
//  Created by Killua Liu on 4/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

#define kMusicVolume    @"musicVolume"
#define kSoundVolume    @"soundVolume"
#define kVoiceVolume    @"voiceVolume"
#define kVibration      @"vibration"
#define kAidSelection   @"aidSelection"
#define kShowNick       @"showNick"

@interface BGGameSetting : BGPopup {
    CCSlider *_musicSlider;
    CCSlider *_soundSlider;
    CCSlider *_voiceSlider;
    BGCheckBox *_vibrationButton;
    BGCheckBox *_aidSelectionButton;
    BGCheckBox *_showNickButton;
}

- (void)valueChanged:(CCSlider *)sender;
- (void)optionChecked:(CCButton *)sender;

@end
