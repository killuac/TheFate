//
//  BGPlayerInformation.h
//  TheFate
//
//  Created by Killua Liu on 3/23/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

@interface BGPlayerInformation : BGPopup {
    CCLabelTTF *_nickNameLabel;
    CCLabelTTF *_levelLabel;
    CCLabelTTF *_winRateLabel;
    CCLabelTTF *_escapeRateLabel;
    
    CCSprite *_heroAvatar;
    CCLabelBMFont *_heroNameLabel;
    CCSprite *_handCardCountFrame;
    CCLabelTTF *_handCardCountLabel;
    CCSprite *_heroInfoFrame;
    CCLabelTTF *_textLabel;
    
    CCButton *_roleButton;
    CCLayoutBox *_roleBox;
    
    CCButton *_leftArrow, *_rightArrow;
}

@property (nonatomic, strong) BGPlayer *player;

- (void)showAllRolesMark:(CCButton *)sender;
- (void)markRole:(CCButton *)sender;

- (void)leftArrowTouched:(CCButton *)sender;
- (void)rightArrowTouched:(CCButton *)sender;

@end
