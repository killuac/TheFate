//
//  BGHeroInformation.h
//  TheFate
//
//  Created by Killua Liu on 4/9/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCardInformation.h"

@interface BGHeroInformation : BGCardInformation {
//    BGTextView *_textView;
}

@property (nonatomic, copy) NSString *heroDesc;

- (void)showFromTopInNode:(CCNode *)node;
- (void)showFromBottomInNode:(CCNode *)node;

@end
