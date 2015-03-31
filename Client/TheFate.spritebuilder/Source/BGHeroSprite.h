//
//  BGHeroSprite.h
//  TheFate
//
//  Created by Killua Liu on 3/24/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCardSprite.h"

@interface BGHeroSprite : BGCardSprite {
    CCButton *_buyButton;
    CCLayoutBox *_priceBox;
    CCLabelTTF *_priceLabel;
}

@property (nonatomic, strong, readonly) CCButton *buyButton;
@property (nonatomic, strong, readonly) CCLayoutBox *priceBox;
@property (nonatomic, strong, readonly) CCLabelTTF *priceLabel;

@end
