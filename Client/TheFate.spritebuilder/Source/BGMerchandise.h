//
//  BGMerchandise.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

//#import "BGTextView.h"

@interface BGMerchandise : CCSprite {
    CCLabelTTF *_nameLabel;
    CCLabelTTF *_priceLabel;
    CCSprite *_imageSprite;
    CCButton *_buyButton;
    CCScrollView *_textView;
}

@property (nonatomic, strong, readonly) CCLabelTTF *nameLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *priceLabel;
@property (nonatomic, strong, readonly) CCButton *buyButton;
//@property (nonatomic, strong, readonly) BGTextView *textView;

@property (nonatomic, copy) NSString *mechandiseId;
@property (nonatomic, copy) NSString *merchandiseDesc;
@property (nonatomic, copy) NSString *imageName;

@end
