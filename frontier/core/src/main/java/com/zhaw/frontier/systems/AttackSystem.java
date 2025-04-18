package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent.BuildingAnimationType;
import com.zhaw.frontier.components.DeathComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent.EnemyAnimationType;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TeamComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.utils.QueueAnimation;

public class AttackSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<AttackComponent> am = ComponentMapper.getFor(
        AttackComponent.class
    );
    private final ComponentMapper<TeamComponent> tm = ComponentMapper.getFor(TeamComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(
        HealthComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pfm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );
    private final ComponentMapper<AnimationQueueComponent> aqm = ComponentMapper.getFor(
        AnimationQueueComponent.class
    );
    private final ComponentMapper<EnemyAnimationComponent> enemyAnimM = ComponentMapper.getFor(
        EnemyAnimationComponent.class
    );
    private final ComponentMapper<BuildingAnimationComponent> buildingAnimM =
        ComponentMapper.getFor(BuildingAnimationComponent.class);
    private final ComponentMapper<DeathComponent> dm = ComponentMapper.getFor(DeathComponent.class);

    private ImmutableArray<Entity> attackers;
    private ImmutableArray<Entity> targets;

    @Override
    public void addedToEngine(Engine engine) {
        attackers =
        engine.getEntitiesFor(
            Family
                .all(
                    PositionComponent.class,
                    AttackComponent.class,
                    TeamComponent.class,
                    HealthComponent.class
                )
                .get()
        );

        targets =
        engine.getEntitiesFor(
            Family.all(PositionComponent.class, TeamComponent.class, HealthComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < attackers.size(); i++) {
            Entity attacker = attackers.get(i);
            AttackComponent attack = am.get(attacker);
            PositionComponent pos = pm.get(attacker);
            TeamComponent team = tm.get(attacker);

            attack.attackCooldown -= deltaTime;
            if (attack.attackCooldown > 0f) continue;

            boolean hasValidTarget = false;

            for (int j = 0; j < targets.size(); j++) {
                Entity target = targets.get(j);
                if (attacker == target) continue;

                TeamComponent targetTeam = tm.get(target);
                if (team.team == targetTeam.team) continue; // Skip allies

                if (!target.isScheduledForRemoval()) {
                    PositionComponent targetPos = pm.get(target);
                    float distance = pos.basePosition.dst(targetPos.basePosition);

                    if (distance <= attack.attackRange) {
                        hasValidTarget = true;

                        HealthComponent health = hm.get(target);
                        health.currentHealth -= attack.damage;
                        attack.attackCooldown = attack.attackInterval;

                        AnimationQueueComponent animQueue = aqm.get(attacker);
                        if (animQueue != null) {
                            Vector2 dir = new Vector2(targetPos.basePosition)
                                .sub(pos.basePosition)
                                .nor();
                            EnemyAnimationType animType = getAttackAnimationDirection(dir);
                            // Clear current animations if any
                            animQueue.queue.clear();

                            QueueAnimation attackAnim = new QueueAnimation();
                            attackAnim.animationType = animType;
                            attackAnim.loop = true;
                            attackAnim.timeLeft = attack.attackInterval;
                            animQueue.queue.add(attackAnim);
                        }

                        if (health.currentHealth <= 0) {
                            health.isDead = true;

                            AnimationQueueComponent deathQueue = aqm.get(target);
                            if (deathQueue != null) {
                                QueueAnimation deathAnim = new QueueAnimation();
                                deathAnim.loop = false;
                                deathAnim.timeLeft = 1.0f;
                                if (enemyAnimM.has(target)) {
                                    deathAnim.animationType = EnemyAnimationType.DEATH;
                                } else if (buildingAnimM.has(target)) {
                                    deathAnim.animationType = BuildingAnimationType.DESTROYING;
                                }
                                deathQueue.queue.clear();
                                deathQueue.queue.add(deathAnim);
                            }

                            if (dm.get(target) == null) {
                                target.add(new DeathComponent(1.0f));
                            }

                            if (pfm.has(attacker)) {
                                PathfindingBehaviourComponent path = pfm.get(attacker);
                                path.resetPath();
                            }
                        }

                        break; // Attack only one target per cycle
                    }
                }
            }

            if (!hasValidTarget) {
                AnimationQueueComponent queue = aqm.get(attacker);
                if (queue != null) {
                    queue.queue.clear();
                }
            }
        }
    }

    private EnemyAnimationType getAttackAnimationDirection(Vector2 dir) {
        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            return dir.x > 0 ? EnemyAnimationType.ATTACK_RIGHT : EnemyAnimationType.ATTACK_LEFT;
        } else {
            return dir.y > 0 ? EnemyAnimationType.ATTACK_UP : EnemyAnimationType.ATTACK_DOWN;
        }
    }
}
