//
//  BGRoomCreation.h
//  TheFate
//
//  Created by Killua Liu on 3/27/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

@class BGRadioButton, BGCheckBox;

@interface BGRoomCreation : BGPopup {
    CCTextField *_passwordText;
    CCLayoutBox *_playTimeButtonBox;
    BGRadioButton *_defaultRoomTypeButton;
    BGRadioButton *_defaultPlayTimeButton;
    BGCheckBox *_noChattingButton;
    
    NSUInteger _capacity;
    NSUInteger _playTime;
    BOOL _isNoChatting;
}

@property (nonatomic, readonly) NSUInteger playTime;

- (void)capacityChecked:(CCButton *)sender;
- (void)playTimeChecked:(CCButton *)sender;
- (void)noChattingChecked:(CCButton *)sender;

@end
