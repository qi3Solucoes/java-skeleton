package br.com.qisi.skeleton.skeleton.controller;

import br.com.qisi.skeleton.skeleton.model.Skeleton;
import br.com.qisi.skeleton.utils.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("skeleton")
public class SkeletonController extends BaseController<Skeleton> {
}
