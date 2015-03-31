//
//  BGAlertPopup.m
//  TheFate
//
//  Created by Killua Liu on 4/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGAlertPopup.h"


@implementation BGAlertPopup

- (void)dismiss
{
    self.visible = NO;
    [super dismiss];
}

- (void)setAllUIViewsHidden:(BOOL)hidden
{
//  Overwrite super method to avoid hide UIViews for store scene
}

@end
