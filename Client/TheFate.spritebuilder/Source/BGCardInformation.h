//
//  BGCardInformation.h
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

@interface BGCardInformation : CCNode {
    CCSprite *_background;
    CCLabelTTF *_textLabel;
}

@property (nonatomic, strong, readonly) CCSprite *background;
@property (nonatomic, strong) CCLabelTTF *textLabel;
@property (nonatomic, readonly) CGSize bgSize;

- (void)showInformationFromTouch:(CGPoint)touchPos;
- (void)showInformationFromTouch:(CGPoint)touchPos anchorPoint:(CGPoint)anchor;

@end
