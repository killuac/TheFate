//
//  BGFeedback.h
//  TheFate
//
//  Created by Killua Liu on 4/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

@interface BGFeedback : BGPopup <UITextFieldDelegate, UITextViewDelegate> {
    UITextField *_titleText;
    UITextView *_contentText;
    CCLabelTTF *_contactLabel;
}

@end
