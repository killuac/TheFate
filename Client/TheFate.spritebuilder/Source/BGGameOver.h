//
//  BGGameOver.h
//  TheFate
//
//  Created by Killua Liu on 3/28/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"
#import "BGResultTable.h"

@interface BGGameOver : BGPopup {
    CCNode *_rootNode;
    CCSprite *_victoryFrame;
    CCSprite *_victoryMark;
    CCSprite *_failureFrame;
    CCSprite *_failureMark;
}

@end
