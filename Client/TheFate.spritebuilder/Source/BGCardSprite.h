//
//  BGCardSprite.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPlayingCard.h"

@interface BGCardSprite : CCSprite {
    CCLabelTTF *_nameLabel;
    CCLabelTTF *_figureLabel;
    CCLabelTTF *_suitsLabel;
    CCLabelTTF *_heroLabel;
    CCSprite *_selectionHalo;
}

@property (nonatomic, strong, readonly) CCLabelTTF *nameLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *figureLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *suitsLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *heroLabel;
@property (nonatomic, strong, readonly) CCSprite *selectionHalo;

@property (nonatomic) BOOL selected;

@end
