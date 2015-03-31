//
//  BGUserInformation.h
//  TheFate
//
//  Created by Killua Liu on 4/12/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

@interface BGUserInformation : BGPopup {
    CCTextField *_nickNameText;
    CCSprite *_userAvatar;
    CCSprite *_vipMark;
    CCLabelTTF *_goldCoinLabel;
    CCLabelTTF *_validTimeLabel;
    
    CCLabelTTF *_levelLabel;
    CCSprite *_expFrame;
    CCSprite *_expProgress;
    CCLabelTTF *_expLabel;
    CCLabelTTF *_winRateLabel;
    CCLabelTTF *_escapeRateLabel;
    CCLabelTTF *_sentinelWinRateLabel;
    CCLabelTTF *_scourgeWinRateLabel;
    CCLabelTTF *_neutralWinRateLabel;
    
    CCLabelTTF *_victoryCountLabel;
    CCLabelTTF *_failureCountLabel;
    CCLabelTTF *_escapeCountLabel;
    CCLabelTTF *_sumKillEnemyCountLabel;
    CCLabelTTF *_sumDoubleKillCountLabel;
    CCLabelTTF *_sumTripleKillCountLabel;
}

@property (nonatomic, readonly) BOOL isDataChanged;

@end
